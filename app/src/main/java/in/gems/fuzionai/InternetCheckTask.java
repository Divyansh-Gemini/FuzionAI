package in.gems.fuzionai;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

// InternetCheckTask class extends the AsyncTask class, which allows us to run a task on a background thread
// & then update the UI thread when the task completes.
public class InternetCheckTask extends AsyncTask<Void, Void, Boolean> {
    private InternetCheckListener listener;

    // defined an interface for callback method onInternetCheckDone()
    public interface InternetCheckListener {
        void onInternetCheckDone(boolean isOnline);
    }

    // constructor to initialize listener
    public InternetCheckTask(InternetCheckListener listener) {
        this.listener = listener;
    }

    // doInBackground() method is executed on a background thread
    // It creates a socket & connects it to a Google DNS server.
    // https://stackoverflow.com/a/27312494/19415431
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(socketAddress, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // onPostExecute() method is executed on the UI thread after doInBackground() completes
    @Override
    protected void onPostExecute(Boolean result) {
        listener.onInternetCheckDone(result);
    }
}