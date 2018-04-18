package z.houbin.site.zdown;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.setup(getApplicationContext());
    }
}
