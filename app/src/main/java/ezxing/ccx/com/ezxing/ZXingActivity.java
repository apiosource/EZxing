package ezxing.ccx.com.ezxing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


import ezxing.ccx.com.ezxing.fragment.ECameraZxingFragment;
import ezxing.ccx.com.ezxing.fragment.ECreateEncodeZxingFragment;
import ezxing.ccx.com.ezxing.fragment.ESelectZxingFragment;

public class ZXingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);

        String   type     = getIntent().getStringExtra("fragmentType");
        Fragment fragment = null;
        switch (type) {
            case "create":
                fragment = new ECreateEncodeZxingFragment();
                break;
            case "camera":
                getSupportActionBar().hide();
                fragment = new ECameraZxingFragment();
                break;
            case "select":
                fragment = new ESelectZxingFragment();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.zxing_fl, fragment).commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
