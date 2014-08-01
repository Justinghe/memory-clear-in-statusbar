package com.android.systemui.statusbar;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import com.android.systemui.R;

public class MemClearActivity extends Activity
{
  private static final int MEM_CLEARED = 0;
  private static final String TAG = "MemClearActivity";
  private long mLastAvailMemory;
  Runnable mClearMemRunnable = new Runnable()
  {
    public void run()
    {
    	mLastAvailMemory=getAvailMemory(MemClearActivity.this);
memClear(MemClearActivity.this);
mHandler.sendEmptyMessageDelayed(MEM_CLEARED, 2000L);
    }
  };
  Handler mHandler = new Handler()
  {
    public void handleMessage(Message message)
    {
      switch (message.what)
      {
   
      case MEM_CLEARED:
     
        super.handleMessage(message);
    
        long nowAvailMemory = getAvailMemory(MemClearActivity.this);
        

        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = Long.valueOf(Math.max(0L,nowAvailMemory-mLastAvailMemory));
        arrayOfObject[1] = Long.valueOf(nowAvailMemory);
        Toast.makeText(MemClearActivity.this, MemClearActivity.this.getString(R.string.mem_cleared, arrayOfObject), 0).show();
       // MemClearActivity.access$002(MemClearActivity.this, l);
      default:
    	  break;
      }
    }
  };
 

  private long getAvailMemory(Context context)
  {
    ActivityManager mActivityManagera = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
   MemoryInfo mMemoryInfo = new MemoryInfo();
    mActivityManagera.getMemoryInfo(mMemoryInfo);
    return mMemoryInfo.availMem / 1048576L;
  }

  private long getTotalMemory(Context context)
  {
    long totalMemory = 0L;
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
      totalMemory = Integer.valueOf(localBufferedReader.readLine().split("\\s+")[1]).intValue();
      localBufferedReader.close();
      return totalMemory / 1024L;
    }
    catch (IOException e)
    {
    }
    return totalMemory;
  }

  private void memClear(Context context)
  {
	  
	  ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
	          // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程   
	      List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager  
               .getRunningAppProcesses();  
	  
	          for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {  
          int pid = appProcess.pid; // pid   
	          String processName = appProcess.processName; // 进程名   
	           Log.i(TAG, "processName: " + processName + "  pid: " + pid);  

	            String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包   
	 
	          // 输出所有应用程序的包名   
	          for (int i = 0; i < pkgNameList.length; i++) {  
	              if (appProcess.importance <= 300)
	                  continue;
               String pkgName = pkgNameList[i];  
                Log.i(TAG, "packageName " + pkgName + " at index " + i+ " in process " + pid); 
                if ((pkgName.equals("com.android.deskclock")) ||
                		(pkgName.equals("com.mediatek.schpwronoff")) || 
                		(pkgName.equals("com.android.launcher")) || 
                		(pkgName.equals("com.android.keyguard")) ||
                		(pkgName.equals("com.android.BatterySaver"))){
                	continue;
                	
                }
                else {
                	 mActivityManager.forceStopPackage(pkgName);
				}
	            
	           }  
	       }  

	 
    System.gc();
    Runtime.getRuntime().runFinalization();
  }



  public void onCreate(Bundle bundle)
  {
  Log.i(TAG, "chunlei"  );  
    super.onCreate(bundle);
    Intent localIntent = getIntent();
    if (localIntent != null)
      localIntent.getAction();
    requestWindowFeature(1);
    new Thread(this.mClearMemRunnable).start();
    finish();
  }
}

