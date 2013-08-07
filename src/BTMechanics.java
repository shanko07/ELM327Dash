import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BTMechanics extends Service {
	
	private final IBinder serviceBinder = new LocalBinder();
	
	public class LocalBinder extends Binder
	{
		BTMechanics getService()
		{
			return BTMechanics.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return serviceBinder;
	}
	
	
	

}
