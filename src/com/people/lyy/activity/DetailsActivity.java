package com.people.lyy.activity;

import com.people.lyy.R;

import android.os.AsyncTask;
import android.os.Bundle;

public class DetailsActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		new DetailsTask().execute();
	}

	class DetailsTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				Thread.sleep(2000);
				return null;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object result) {
			DetailsActivity.this.finish();
		}

	}
}
