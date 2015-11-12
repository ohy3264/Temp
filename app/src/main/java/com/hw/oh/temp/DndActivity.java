package com.hw.oh.temp;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hw.oh.fragment.Fragment_Swap;
import com.hw.oh.model.PartTimeInfo;

import java.util.ArrayList;

public class DndActivity extends AppCompatActivity {
  public static final String TAG = "DndActivity";
  private ArrayList<PartTimeInfo> mAlbaInfoList = new ArrayList<PartTimeInfo>();
  private Toolbar mToolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);//ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mToolbar.setTitle("순서변경");
    setSupportActionBar(mToolbar);
    final ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);

    Bundle bundle = getIntent().getBundleExtra("bundle");
    mAlbaInfoList = (ArrayList<PartTimeInfo>) bundle.getSerializable("ObjectDataList");
    for(int i = 0; i < mAlbaInfoList.size(); i++){
      Log.i(TAG, mAlbaInfoList.get(i).getAlbaname());
    }

    //Fragment Container
    Fragment_Swap frag_dnd = new Fragment_Swap(mAlbaInfoList);
    FragmentManager mFragManager = getFragmentManager();
    mFragManager.beginTransaction().replace(R.id.container, frag_dnd).commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_swip, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case android.R.id.home:
        finish();
        break;
      case R.id.action_swap:
        break;

    }
    return super.onOptionsItemSelected(item);
  }
}