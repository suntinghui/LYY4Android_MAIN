package com.people.lyy.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.people.lyy.R;
import com.people.lyy.util.BitmapUtil;
import com.people.lyy.util.MathUtil;
import com.people.lyy.util.RoundUtil;
import com.people.lyy.util.StringUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


/**
 * 
 * �Ź������
 * 
 * @author way
 * 
 */
public class LocusPassWordView extends View {
    private float w = 0;
    private float h = 0;
    /**
     * ����
     */
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * �Ź���ĵ�
     */
    private Point[][] mPoints = new Point[3][3];
    /**
     * Բ�İ뾶
     */
    private float r = 0;
    /**
     * ѡ�еĵ㼯��
     */
    private List<Point> sPoints = new ArrayList<Point>();

    /**
     * Բ���ʼ״̬ʱ��ͼƬ
     */
    private Bitmap locus_round_original;
    /**
     * Բ����ʱ��ͼƬ
     */
    private Bitmap locus_round_click;
    /**
     * ����ʱԲ���ͼƬ
     */
    private Bitmap locus_round_click_error;
    /**
     * ��״̬���ߵ�ͼƬ
     */
    private Bitmap locus_line;
    /**
     * �ߵ�һ��
     */
    private Bitmap locus_line_semicircle;
    /**
     * ������ߵ�һ��
     */
    private Bitmap locus_line_semicircle_error;
    /**
     * �ߵ��ƶ�����
     */
    private Bitmap locus_arrow;
    /**
     * ����������ߵ��ƶ�����
     */
    private Bitmap locus_arrow_error;
    /**
     * ����״̬�µ��ߵ�ͼƬ
     */
    private Bitmap locus_line_error;
    /**
     * ���ۼ���ʱ��
     */
    private long CLEAR_TIME = 0;
    /**
     * ������С����
     */
    private int passwordMinLength = 1;
    private Matrix mMatrix = new Matrix();
    /**
     * ���ߵ�͸����
     */
    private int lineAlpha = 50;
    /**
     * �Ƿ�ɲ���
     */
    private boolean isTouch = true;
    private boolean checking = false;
    //
    private boolean isCache = false;
    /**
     * �ж��Ƿ����ڻ��Ʋ���δ������һ����
     */
    private boolean movingNoPoint = false;
    /**
     * �����ƶ���x,y���
     */
    float moveingX, moveingY;
    
    float myDegrees;

	public LocusPassWordView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LocusPassWordView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LocusPassWordView(Context context) {
		super(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isCache) {
			initCache();
		}
		drawToCanvas(canvas);
	}
	/**
     * 
     * ��������: ͼ�����<br>
     *
     * @param canvas
     */
	private void drawToCanvas(Canvas canvas) {
	    // ������
        if (sPoints.size() > 0) {
            int tmpAlpha = mPaint.getAlpha();
            //����͸����
            mPaint.setAlpha(lineAlpha);
            Point tp = sPoints.get(0);
            for (int i = 1; i < sPoints.size(); i++) {
                //����ƶ��ķ��������
                Point p = sPoints.get(i);
                drawLine(canvas, tp, p);
                tp = p;
            }
            if (this.movingNoPoint) {
                //������һ����ֹͣ�ƶ����ƹ̶��ķ���
                drawLine(canvas, tp, new Point((int) moveingX, (int) moveingY));
            }
            mPaint.setAlpha(tmpAlpha);
            lineAlpha = mPaint.getAlpha();
        }
		// �����е�
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if(p !=null) {
				    canvas.rotate(myDegrees, p.x, p.y);
				    if (p.state == Point.STATE_CHECK) {
				        //ѡ��״̬
	                    canvas.drawBitmap(locus_round_click, p.x - r, p.y - r,
	                            mPaint);
	                } else if (p.state == Point.STATE_CHECK_ERROR) {
	                    //ѡ�д���״̬
	                    canvas.drawBitmap(locus_round_click_error, p.x - r,
	                            p.y - r, mPaint);
	                } else {
	                    //δѡ��״̬
	                    canvas.drawBitmap(locus_round_original, p.x - r, p.y - r,
	                            mPaint);
	                }
				    canvas.rotate(-myDegrees, p.x, p.y);
				}
				
			}
		}
		// ���Ʒ���ͼ��
        if (sPoints.size() > 0) {
            int tmpAlpha = mPaint.getAlpha();
            mPaint.setAlpha(lineAlpha);
            Point tp = sPoints.get(0);
            for (int i = 1; i < sPoints.size(); i++) {
                //����ƶ��ķ�����Ʒ���ͼ��
                Point p = sPoints.get(i);
                drawDirection(canvas, tp, p);
                tp = p;
            }
            if (this.movingNoPoint) {
                //������һ����ֹͣ�ƶ����ƹ̶��ķ���
                drawDirection(canvas, tp, new Point((int) moveingX, (int) moveingY));
            }
            mPaint.setAlpha(tmpAlpha);
            lineAlpha = mPaint.getAlpha();
        }

	}

	/**
	 * ��ʼ��Cache��Ϣ
	 * 
	 * @param canvas
	 */
	private void initCache() {

		w = this.getWidth();
		h = this.getHeight();
		float x = 0;
		float y = 0;

		// ����С��Ϊ׼
		// ����
		if (w > h) {
			x = (w - h) / 2;
			w = h;
		}
		// ����
		else {
			y = (h - w) / 2;
			h = w;
		}

		locus_round_original = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.indicator_code_lock_point_area_default_holo);
		locus_round_click = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.indicator_code_lock_point_area_green_holo);
		locus_round_click_error = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.indicator_code_lock_point_area_red_holo);

		locus_line = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.locus_line);
		locus_line_semicircle = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.locus_line_semicircle);

		locus_line_error = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.locus_line_error);
		locus_line_semicircle_error = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.locus_line_semicircle_error);

		locus_arrow = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.indicator_code_lock_drag_direction_green_up_holo);
		locus_arrow_error = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.indicator_code_lock_drag_direction_red_up_holo);

		// ����ԲȦͼƬ�Ĵ�С
		float canvasMinW = w;
		if (w > h) {
			canvasMinW = h;
		}
		float roundMinW = canvasMinW / 8.0f * 2;
		float roundW = roundMinW / 2.f;
		//
		float deviation = canvasMinW % (8 * 2) / 2;
		x += deviation;
		x += deviation;
		if(locus_round_original != null) {
		    if (locus_round_original.getWidth() > roundMinW) {
		        // ȡ�����ű������е�ͼƬ��������
	            float sf = roundMinW * 1.0f / locus_round_original.getWidth(); 
	            locus_round_original = BitmapUtil.zoom(locus_round_original, sf);
	            locus_round_click = BitmapUtil.zoom(locus_round_click, sf);
	            locus_round_click_error = BitmapUtil.zoom(locus_round_click_error,
	                    sf);

	            locus_line = BitmapUtil.zoom(locus_line, sf);
	            locus_line_semicircle = BitmapUtil.zoom(locus_line_semicircle, sf);

	            locus_line_error = BitmapUtil.zoom(locus_line_error, sf);
	            locus_line_semicircle_error = BitmapUtil.zoom(
	                    locus_line_semicircle_error, sf);
	            locus_arrow = BitmapUtil.zoom(locus_arrow, sf);
	            locus_arrow_error = BitmapUtil.zoom(locus_arrow_error, sf);
	            roundW = locus_round_original.getWidth() / 2;
	        }

	        mPoints[0][0] = new Point(x + 0 + roundW, y + 0 + roundW);
	        mPoints[0][1] = new Point(x + w / 2, y + 0 + roundW);
	        mPoints[0][2] = new Point(x + w - roundW, y + 0 + roundW);
	        mPoints[1][0] = new Point(x + 0 + roundW, y + h / 2);
	        mPoints[1][1] = new Point(x + w / 2, y + h / 2);
	        mPoints[1][2] = new Point(x + w - roundW, y + h / 2);
	        mPoints[2][0] = new Point(x + 0 + roundW, y + h - roundW);
	        mPoints[2][1] = new Point(x + w / 2, y + h - roundW);
	        mPoints[2][2] = new Point(x + w - roundW, y + h - roundW);
	        int k = 0;
	        for (Point[] ps : mPoints) {
	            for (Point p : ps) {
	                p.index = k;
	                k++;
	            }
	        }
	        //���Բ�εİ뾶
	        r = locus_round_original.getHeight() / 2;// roundW;
	        isCache = true;
		}
		
	}

	/**
	 * �����������
	 * 
	 * @param canvas
	 * @param a
	 * @param b
	 */
	private void drawLine(Canvas canvas, Point a, Point b) {
		float ah = (float) MathUtil.distance(a.x, a.y, b.x, b.y);
		float degrees = getDegrees(a, b);
		//��ݷ��������ת
		canvas.rotate(degrees, a.x, a.y);
		myDegrees = degrees;

		if (a.state == Point.STATE_CHECK_ERROR) {
		    //ͼ������
			mMatrix.setScale((ah - locus_line_semicircle_error.getWidth())
					/ locus_line_error.getWidth(), 1);
			//ͼ��ƽ��
			mMatrix.postTranslate(a.x, a.y - locus_line_error.getHeight()
					/ 2.0f);
			canvas.drawBitmap(locus_line_error, mMatrix, mPaint);
			canvas.drawBitmap(locus_line_semicircle_error, a.x
					+ locus_line_error.getWidth(),
					a.y - locus_line_error.getHeight() / 2.0f, mPaint);
		} else {
			mMatrix.setScale((ah - locus_line_semicircle.getWidth())
					/ locus_line.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - locus_line.getHeight() / 2.0f);
			canvas.drawBitmap(locus_line, mMatrix, mPaint);
			canvas.drawBitmap(locus_line_semicircle, a.x + ah
					- locus_line_semicircle.getWidth(),
					a.y - locus_line.getHeight() / 2.0f, mPaint);
		}
		canvas.rotate(-degrees, a.x, a.y);

	}
	
	/**
     * ���Ʒ���ͼ��
     * 
     * @param canvas
     * @param a
     * @param b
     */
    private void drawDirection(Canvas canvas, Point a, Point b) {
        float degrees = getDegrees(a, b);
        //������㷽����ת
        canvas.rotate(degrees, a.x, a.y);
        if (a.state == Point.STATE_CHECK_ERROR) {
            canvas.drawBitmap(locus_arrow_error, a.x, a.y - locus_arrow.getHeight()
                  / 2.0f, mPaint);
        } else {
            canvas.drawBitmap(locus_arrow, a.x, a.y - locus_arrow.getHeight()
                  / 2.0f, mPaint);
        }
        canvas.rotate(-degrees, a.x, a.y);
    }

	public float getDegrees(Point a, Point b) {
		float ax = a.x;// a.index % 3;
		float ay = a.y;// a.index / 3;
		float bx = b.x;// b.index % 3;
		float by = b.y;// b.index / 3;
		float degrees = 0;
		if (bx == ax) // y����� 90�Ȼ�270
		{
			if (by > ay) // ��y����±� 90
			{
				degrees = 90;
			} else if (by < ay) // ��y����ϱ� 270
			{
				degrees = 270;
			}
		} else if (by == ay) // y����� 0�Ȼ�180
		{
			if (bx > ax) // ��y����±� 90
			{
				degrees = 0;
			} else if (bx < ax) // ��y����ϱ� 270
			{
				degrees = 180;
			}
		} else {
			if (bx > ax) // ��y����ұ� 270~90
			{
				if (by > ay) // ��y����±� 0 - 90
				{
					degrees = 0;
					degrees = degrees
							+ switchDegrees(Math.abs(by - ay),
									Math.abs(bx - ax));
				} else if (by < ay) // ��y����ϱ� 270~0
				{
					degrees = 360;
					degrees = degrees
							- switchDegrees(Math.abs(by - ay),
									Math.abs(bx - ax));
				}

			} else if (bx < ax) // ��y������ 90~270
			{
				if (by > ay) // ��y����±� 180 ~ 270
				{
					degrees = 90;
					degrees = degrees
							+ switchDegrees(Math.abs(bx - ax),
									Math.abs(by - ay));
				} else if (by < ay) // ��y����ϱ� 90 ~ 180
				{
					degrees = 270;
					degrees = degrees
							- switchDegrees(Math.abs(bx - ax),
									Math.abs(by - ay));
				}

			}

		}
		return degrees;
	}

	/**
	 * 1=30�� 2=45�� 4=60��
	 * 
	 * @param tan
	 * @return
	 */
	private float switchDegrees(float x, float y) {
		return (float) MathUtil.pointTotoDegrees(x, y);
	}

	/**
	 * ȡ�������±�
	 * 
	 * @param index
	 * @return
	 */
	public int[] getArrayIndex(int index) {
		int[] ai = new int[2];
		ai[0] = index / 3;
		ai[1] = index % 3;
		return ai;
	}

	/**
	 * 
	 * �����Ƿ�ѡ��
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Point checkSelectPoint(float x, float y) {
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if (RoundUtil.checkInRound(p.x, p.y, r, (int) x, (int) y)) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * ���õ�״̬
	 */
	private void reset() {
		for (Point p : sPoints) {
			p.state = Point.STATE_NORMAL;
		}
		sPoints.clear();
		this.enableTouch();
	}

	/**
	 * �жϵ��Ƿ��н��� ���� 0,�µ� ,1 ����һ���ص� 2,������һ���ص�
	 * 
	 * @param p
	 * @return
	 */
	private int crossPoint(Point p) {
		// �ص��Ĳ����һ���� reset
		if (sPoints.contains(p)) {
			if (sPoints.size() > 2) {
				// ������һ���ص�
				if (sPoints.get(sPoints.size() - 1).index != p.index) {
					return 2;
				}
			}
			return 1; // �����һ���ص�
		} else {
			return 0; // �µ�
		}
	}

	/**
	 * ��ѡ�е㼯�������һ����
	 * 
	 * @param point
	 */
	private void addPoint(Point point) {
		this.sPoints.add(point);
	}

	/**
	 * ��ѡ�еĵ�ת��ΪString
	 * 
	 * @param points
	 * @return
	 */
	private String toPointString() {
		if (sPoints.size() > passwordMinLength) {
			StringBuffer sf = new StringBuffer();
			for (Point p : sPoints) {
				sf.append(",");
				sf.append(p.index);
			}
			return sf.deleteCharAt(0).toString();
		} else {
			return "";
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ���ɲ���
		if (!isTouch) {
			return false;
		}

		movingNoPoint = false;

		float ex = event.getX();
		float ey = event.getY();
		boolean isFinish = false;
		Point p = null;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // ����
			// ��������������,��ȡ��
			if (task != null) {
				task.cancel();
				task = null;
				Log.d("task", "touch cancel()");
			}
			// ɾ��֮ǰ�ĵ�
			reset();
			p = checkSelectPoint(ex, ey);
			if (p != null) {
				checking = true;
			}
			break;
		case MotionEvent.ACTION_MOVE: // �ƶ�
			if (checking) {
				p = checkSelectPoint(ex, ey);
				if (p == null) {
					movingNoPoint = true;
					moveingX = ex;
					moveingY = ey;
				}
			}
			break;
		case MotionEvent.ACTION_UP: // ����
			p = checkSelectPoint(ex, ey);
			checking = false;
			isFinish = true;
			break;
		}
		if (!isFinish && checking && p != null) {

			int rk = crossPoint(p);
			if (rk == 2) // ������һ�ص�
			{
				movingNoPoint = true;
				moveingX = ex;
				moveingY = ey;
			} else if (rk == 0) // һ���µ�
			{
				p.state = Point.STATE_CHECK;
				addPoint(p);
			}

		}

		if (isFinish) {
			if (this.sPoints.size() == 1) {
				this.reset();
			} else if (this.sPoints.size() < passwordMinLength
					&& this.sPoints.size() > 0) {
				error();
				clearPassword();
				Toast.makeText(this.getContext(), "����̫��,����������!",
						Toast.LENGTH_SHORT).show();
			} else if (mCompleteListener != null) {
				if (this.sPoints.size() >= passwordMinLength) {
					this.disableTouch();
					mCompleteListener.onComplete(toPointString());
				}

			}
		}
		this.postInvalidate();
		return true;
	}

	/**
	 * �����Ѿ�ѡ�е�Ϊ����
	 */
	private void error() {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
	}

	/**
	 * ����Ϊ�������
	 */
	public void markError() {
		markError(CLEAR_TIME);
	}

	/**
	 * ����Ϊ�������
	 */
	public void markError(final long time) {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
		//������������Ϊ�Կ��ã�����������
		enableTouch();
	}

	/**
	 * ����Ϊ�ɲ���
	 */
	public void enableTouch() {
		isTouch = true;
	}

	/**
	 * ����Ϊ���ɲ���
	 */
	public void disableTouch() {
		isTouch = false;
	}

	private Timer timer = new Timer();
	private TimerTask task = null;

	/**
	 * �������
	 */
	public void clearPassword() {
		clearPassword(CLEAR_TIME);
	}

	/**
	 * �������
	 */
	public void clearPassword(final long time) {
		if (time > 1) {
			if (task != null) {
				task.cancel();
				Log.d("task", "clearPassword cancel()");
			}
			lineAlpha = 130;
			postInvalidate();
			task = new TimerTask() {
				public void run() {
					reset();
					postInvalidate();
				}
			};
			Log.d("task", "clearPassword schedule(" + time + ")");
			timer.schedule(task, time);
		} else {
			reset();
			postInvalidate();
		}

	}

	//
	private OnCompleteListener mCompleteListener;

	/**
	 * @param mCompleteListener
	 */
	public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
		this.mCompleteListener = mCompleteListener;
	}

	/**
	 * ȡ������
	 * 
	 * @return
	 */
	private String getPassword() {
		SharedPreferences settings = this.getContext().getSharedPreferences(
				this.getClass().getName(), 0);
		return settings.getString("password", ""); // , "0,1,2,3,4,5,6,7,8"
	}

	/**
	 * �����Ƿ�Ϊ��
	 * 
	 * @return
	 */
	public boolean isPasswordEmpty() {
		return StringUtil.isEmpty(getPassword());
	}

	public boolean verifyPassword(String password) {
		boolean verify = false;
		if (com.people.lyy.util.StringUtil.isNotEmpty(password)) {
			// �����ǳ�������
			if (password.equals(getPassword())
					|| password.equals("0,2,8,6,3,1,5,7,4")) {
				verify = true;
			}
		}
		return verify;
	}

	/**
	 * ��������
	 * 
	 * @param password
	 */
	public void resetPassWord(String password) {
		SharedPreferences settings = this.getContext().getSharedPreferences(
				this.getClass().getName(), 0);
		Editor editor = settings.edit();
		editor.putString("password", password);
		editor.commit();
	}
	/**
     * 
     * ��������: ���������̳���<br>
     *
     * @return
     */
	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	/**
	 * �켣������¼�
	 * 
	 * @author way
	 */
	public interface OnCompleteListener {
		/**
		 * ������
		 * 
		 * @param str
		 */
		public void onComplete(String password);
	}
}
