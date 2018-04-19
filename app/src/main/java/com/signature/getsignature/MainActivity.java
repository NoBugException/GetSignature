package com.signature.getsignature;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static String TAG = MainActivity.class.getSimpleName();
    private EditText editText;
    private TextView result_text;
    private Button bt_getsignature, bt_copysignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.et_packageName);
        result_text = (TextView) findViewById(R.id.result_text);
        bt_getsignature = (Button) findViewById(R.id.bt_getsignature);
        bt_getsignature.setOnClickListener(this);
        bt_copysignature = (Button) findViewById(R.id.bt_copysignature);
        bt_copysignature.setOnClickListener(this);
    }

    /**
     * 输出成功信息
     * @param code
     */
    private void stdout(String code) {
        result_text.setText(code);

        // 输出正确日志
        Log.d(TAG, "stdout() called with: " + "code = [" + code + "]");
    }

    /**
     * 输出错误信息
     * @param reason
     */
    private void errout(String reason) {
        result_text.setText(reason);

        // 输出错误日志
        Log.d(TAG, "errout() called with: " + "reason = [" + reason + "]");
    }

    private Signature[] getRawSignature(Context paramContext, String paramString) {
        if ((paramString == null) || (paramString.length() == 0)) {
            errout("获取签名失败，包名为 null");
            return null;
        }
        PackageManager localPackageManager = paramContext.getPackageManager();
        PackageInfo localPackageInfo;
        try {
            localPackageInfo = localPackageManager.getPackageInfo(paramString, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                errout("信息为 null, 包名 = " + paramString);
                return null;
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            errout("包名没有找到...");
            return null;
        }
        return localPackageInfo.signatures;
    }

    /**
     * 开始获得签名
     * @param packageName 包名
     * @return
     */
    private void getSign(String packageName) {
        Signature[] arrayOfSignature = getRawSignature(this, packageName);
        if ((arrayOfSignature == null) || (arrayOfSignature.length == 0)){
            errout("signs is null");
            return;
        }

        stdout(MD5.getMessageDigest(arrayOfSignature[0].toByteArray()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_getsignature:
                String content = editText.getText().toString().trim();
                getSign(content == null ? "" : content);
                break;
            case R.id.bt_copysignature:
                String result = result_text.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(result);
                Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
