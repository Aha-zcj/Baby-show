package com.babyshow.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babyshow.R;
import com.babyshow.utils.LogUtils;
import com.babyshow.utils.SingleThreadTask;

public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";

	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	
	private SingleThreadTask mLoginTask;
	
	private ValidateCompletiondHandler mHandler = new ValidateCompletiondHandler() {
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 0) {
				//Lanuch the MainActivity
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				//Destroy the loginActivity
				finish();
			} else {
				// 长度没有必要就不要用自定义的吧,切记魔数，如果要自定义尽量同意写在一个地方，让后通过常量引用
				Toast.makeText(LoginActivity.this,
						getResources().getString(R.string.toast_validate_fail),
						Toast.LENGTH_SHORT).show();
				;
			}
		}
	};
	
	abstract class ValidateCompletiondHandler extends Handler{
		public final static int MSG_VALIDATE_COMPLETION = 0;
	}

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
				//new LoginTask().execute();
				if (isEmailEmpty()) {
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
					doLogin();
				}
				
				
				
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
	 * 登陆：开启一个新建程作登陆
	 */
	public void doLogin() {
		
		final String email = mEmailEditText.getText().toString().trim();
		final String password = mPasswordEditText.getText().toString().trim();
		
		//如果已存在正在工作的线程，停止
		if(mLoginTask !=null) 
			mLoginTask.stopTask();
		//新建线程
		mLoginTask = new SingleThreadTask() {

			@Override
			protected void task() throws Exception {
				// TODO Auto-generated method stub
					//通回验证结果
					int code = validate(email, password);
					//获得消息
					Message msg = mHandler.obtainMessage();
					//设置数招
					msg.arg1 = code;
					//发送消息
					mHandler.sendMessage(msg);
				}
		};
		//线程开始工作
		mLoginTask.startTask();
	}
	
	/**
	 * 网络验证
	 * @param email 登陆邮箱
	 * @param password 登陆密码
	 * @return 验证成功返回0，失败为101
	 */
	public int validate(String email, String password) {
		
		HttpURLConnection urlConnection = null;
		try {
			String urlString = getResources().getString(
					R.string.http_do_login);
			//新建 URL
			URL url = new URL(urlString);
			//连接服务器
			urlConnection = (HttpURLConnection) url.openConnection();
			
			// 设置上传方式，注意与服务端沟通，问明白提交方式是什么，咱们这个是用post表单提交
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			
			// 记得重新设置超时，默认超时比较短
			urlConnection.setReadTimeout(20 * 1000);
			
			OutputStream output = new BufferedOutputStream(
					urlConnection.getOutputStream());
			
			// post提交的数据和get方式是一样的，这里和后台沟通下是否进行加密改进
			StringBuilder sb = new StringBuilder();
			sb.append("email=").append(email).append("&password=").append(password);
			output.write(sb.toString().getBytes());
			output.flush();
			output.close();
			
			// 读取返回值，记得用循环方式来写，比较通用
			InputStream input = new BufferedInputStream(
					urlConnection.getInputStream());
			byte[] buffer = new byte[1024];
			int length = 0;
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			while((length = input.read(buffer, 0, buffer.length)) > 0) {
				outStream.write(buffer, 0, length);
			}
			input.close();
			byte[] data = outStream.toByteArray();
			String result = new String(data);
			
			//返回数据以JSON作载体
			JSONObject backData = new JSONObject(result.substring(1).trim());
			
			// 最后返回值最好不要用boolean，通常比较常用int来表示错误码，最好整理出一套来
			return backData.getInt("code");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		
		return 101;
	}

	/**
	 * 验证 输入账号 是否为空
	 * 
	 * @return 空则返回true, 否则返回false
	 */
	public boolean isEmailEmpty() {

		String email = mEmailEditText.getText().toString().trim();

		return email.equals("");
	}

	/**
	 * 验证输入密码是否为空
	 * 
	 * @return 空则返回true, 否则返回false
	 */
	public boolean isPasswordEmpty() {

		String password = mPasswordEditText.getText().toString().trim();

		return password.equals("");
	}

	
}
