package com.babyshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babyshow.R;

public class LoginActivity extends Activity{
	
	private static final String TAG="LoginActivity";
	
	private EditText mLoginIdEditText;
	private EditText mPasswordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		prepareData();
	}
	
	public void prepareData() {
		
		mLoginIdEditText = (EditText) findViewById(R.id.et_login_id);
		mPasswordEditText = (EditText) findViewById(R.id.et_login_password);
		
		//Button登陆
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		//为登陆按钮绑定监听器
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//验证输入是否完整
				if(isLoginIdEmpty()) {
					Toast.makeText(LoginActivity.this, "请输入登陆帐号", 500).show();
				} else if (isPasswordEmpty()) {
					Toast.makeText(LoginActivity.this, "请输入密码", 500).show();
				} else {
					//从服务器中获取验证
				}
			}
		});
		//Button注册
		Button btnRegister = (Button) findViewById(R.id.btn_register);
		//绑定监听器
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		//TextView-"忘记密码？"
		TextView forgetTextView = (TextView) findViewById(R.id.tv_forget_password);
		//绑定监听器
		forgetTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	/**
	 * 验证 输入账号 是否为空
	 * @return
	 */
	public boolean isLoginIdEmpty() {
		
		String loginId = mLoginIdEditText.getText().toString().trim();
		
		return loginId.equals("");
	}
	/**
	 * 验证输入密码是否为空
	 * @return
	 */
	public boolean isPasswordEmpty() {
		
		String password = mPasswordEditText.getText().toString().trim();
		
		return password.equals("");
	}
}
