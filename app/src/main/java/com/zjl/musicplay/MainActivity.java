package com.zjl.musicplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_love;
    private RelativeLayout jump_local_music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_love=(TextView) findViewById(R.id.tv_love);
        tv_love.setOnClickListener(this);

        jump_local_music=(RelativeLayout)findViewById(R.id.jump_local_music);
        jump_local_music.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_love:
                Toast.makeText(getApplicationContext(),"我喜欢",Toast.LENGTH_LONG).show();
                break;
            case R.id.jump_local_music:
                Toast.makeText(getApplicationContext(),"跳到本地音乐列表",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(this,MusicLocalActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.scale_in,R.anim.scale_out);
                break;

        }
    }
}
