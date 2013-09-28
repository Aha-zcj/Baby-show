package com.babyshow.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babyshow.R;
import com.babyshow.utils.LogUtils;

public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";

	private EditText mEmailEditText;
	private EditText mPasswordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		prepareData();
	}

	public void prepareData() {

		mEmailEditText = (EditText) findViewById(R.id.et_email);
		mPasswordEditText = (EditText) findViewById(R.id.et_password);
		

		// Button登陆
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		// 为登陆按钮绑定监听器
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 验证输入是否完整
				new LoginTask().execute();
				/*if (isLoginIdEmpty()) {
					Toast.makeText(
							LoginActivity.this,
							getResources()
									.getString(R.string.toast_input_email), 500)
							.show();
				} else if (isPasswordEmpty()) {
					Toast.makeText(
							LoginActivity.this,
							getResources().getString(
									R.string.toast_input_password), 500).show();
				} else {
					// 从服务器中获取验证
					new LoginTask().execute();
				}*/
			}
		});
		// Button注册
		Button btnRegister = (Button) findViewById(R.id.btn_register);
		// 绑定监听器
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});
		// TextView-"忘记密码？"
		TextView forgetTextView = (TextView) findViewById(R.id.tv_forget_password);
		// 绑定监听器
		forgetTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 验证 输入账号 是否为空
	 * 
	 * @return
	 */
	public boolean isLoginIdEmpty() {

		String loginId = mEmailEditText.getText().toString().trim();

		return loginId.equals("");
	}

	/**
	 * 验证输入密码是否为空
	 * 
	 * @return
	 */
	public boolean isPasswordEmpty() {

		String password = mPasswordEditText.getText().toString().trim();

		return password.equals("");
	}

	private class LoginTask extends AsyncTask<Void, Void, Boolean> {

		private final static String TAG = "LoginTask";

		private String mEmail;
		private String mPassword;

		public LoginTask() {
			mEmail = mEmailEditText.getText().toString().trim();
			mPassword = mPasswordEditText.getText().toString().trim();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			HttpURLConnection urlConnection = null;
			try {
				String urlString = getResources().getString(
						R.string.http_do_login);
				//新建 URL
				URL url = new URL(urlString);
				//连接服务器
				urlConnection = (HttpURLConnection) url.openConnection();
				//设置上传
				urlConnection.setDoOutput(true);
				urlConnection.setChunkedStreamingMode(0);
				
				OutputStream output = new BufferedOutputStream(
						urlConnection.getOutputStream());
				//请求数据以JSON作载体
				JSONObject requestData = new JSONObject();
				requestData.put("email", mEmail);
				requestData.put("password", mPassword);
				//将JSON写入输出流
				output.write(requestData.toString().getBytes());

				byte[] buffer = new byte[1024];
				InputStream input = new BufferedInputStream(
						urlConnection.getInputStream());
				input.read(buffer);
				//返回数据以JSON作载体
				JSONObject backData = new JSONObject(buffer.toString());
				LogUtils.debug(TAG, backData.toString());
				return backData.getBoolean("code");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean isLogin) {
			if (isLogin) {
				//Lanuch the MainActivity
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				//Destroy the loginActivity
				finish();
			} else {
				Toast.makeText(LoginActivity.this,
						getResources().getString(R.string.toast_validate_fail),
						200).show();
				;
			}
		}

	}
}
