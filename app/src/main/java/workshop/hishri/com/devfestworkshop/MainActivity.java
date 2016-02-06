package workshop.hishri.com.devfestworkshop;

import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.gesture.Sgesture;
import com.samsung.android.sdk.gesture.SgestureHand;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Sgesture myGesture ;
    private SgestureHand myGestureHand ;


    protected ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager() );


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        myGesture = new Sgesture() ;

        try {
            myGesture.initialize( getApplicationContext() );
        }catch(SsdkUnsupportedException ssdkex){
            Toast.makeText(getApplicationContext(), "Gesture detection not supported", Toast.LENGTH_LONG).show();
        }catch(IllegalArgumentException e){
            Toast.makeText(getApplicationContext(), "context error", Toast.LENGTH_LONG).show();
        }

        myGestureHand = new SgestureHand(Looper.getMainLooper(), myGesture);
        myGestureHand.start(Sgesture.TYPE_HAND_PRIMITIVE, changeListener);

        // auth

        Spass sPass = new Spass();           // Fingerprint sensor Manager
        try{
            sPass.initialize(getApplicationContext());          // Does this device support Samsung SDK?
            if( sPass.isFeatureEnabled( Spass.DEVICE_FINGERPRINT ) )
            {
                SpassFingerprint taSpassFingerprint = new SpassFingerprint(this);
                if( !taSpassFingerprint.hasRegisteredFinger() )    // supported but no fingerprint saved
                {
                    taSpassFingerprint.registerFinger(this, taRegisterListener);

                }else{
                    taSpassFingerprint.startIdentifyWithDialog(this, listener, true);
                }
            }
        }catch(SsdkUnsupportedException ex)
        {

        }catch (UnsupportedOperationException e)
        {

        }
    }


    final SgestureHand.ChangeListener changeListener = new SgestureHand.ChangeListener(){

        @Override
        public void onChanged(SgestureHand.Info info) {
            if (info.getType() == Sgesture.TYPE_HAND_PRIMITIVE)
            {

                Toast.makeText(getApplicationContext(), "Gesture detected | angle: "+info.getAngle()+" | speed :"+info.getSpeed(), Toast.LENGTH_SHORT).show();

                if(info.getAngle() > 0 && info.getAngle() < 180 )
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                else
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);

            }
        }
    };

    private SpassFingerprint.IdentifyListener listener = new SpassFingerprint.IdentifyListener()
    {

        @Override
        public void onFinished(int eventStatus)
        {
            // identification finished
            if(eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS)
            {
                Toast.makeText(getApplicationContext(), "authenticated", Toast.LENGTH_LONG).show();

            }else if(eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS)
            {

            }else {

            }
        }

        @Override
        public void onReady() {
            // after startIdentify() calling

        }

        @Override
        public void onStarted()
        {
            // user touches fingerprint sensor after startIdentify() calling
            Toast.makeText(getApplicationContext(), "Reading ..", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCompleted() {

        }
    };


    private SpassFingerprint.RegisterListener taRegisterListener = new SpassFingerprint.RegisterListener()
    {

        @Override
        public void onFinished() {

        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        myGestureHand.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position)
            {
                case 0  : return new BlankFragment() ;
                case 1  : return new secondFragment() ;
                case 2  : return new ThirdFragment() ;

            }

            return new BlankFragment();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
