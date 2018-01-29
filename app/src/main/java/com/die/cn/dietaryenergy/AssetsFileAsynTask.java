package com.die.cn.dietaryenergy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * {此处填写描述信息}
 * Created by huangchaobo on 2018/1/25 15.
 * 邮箱：huangchaobo@miao.cn
 */

public class AssetsFileAsynTask extends AsyncTask<Void, Void, Void> {
    private Context mContext = null;
    private DietDBManager dbManager = null;

    public AssetsFileAsynTask(Context context) {
        this.mContext = context;
    }


    @Override
    protected Void doInBackground(Void... params) {
        dbManager = DietDBManager.getInstance(mContext);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        DietInfoBean extraVoiceInfo = dbManager.getInfoById(44 + "");
        Log.d("huang",extraVoiceInfo.toString());
        Toast.makeText(mContext.getApplicationContext(),"assets file保存到数据库完成",Toast.LENGTH_SHORT).show();
    }

}