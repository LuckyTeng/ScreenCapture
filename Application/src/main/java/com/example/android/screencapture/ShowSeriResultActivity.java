package com.example.android.screencapture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class ShowSeriResultActivity extends Activity implements View.OnClickListener {

    private Button _button2;
    private Button _button3;
    BeanDemo beanDemo;
    ArrayList<BeanDemo> array ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seri_result);

        _button2 = findViewById(R.id.button2);
        _button2.setOnClickListener(this);

        _button3 = findViewById(R.id.button3);
        _button3.setOnClickListener(this);

        Intent intent = getIntent();

        beanDemo = (BeanDemo) getIntent().getSerializableExtra("key_one");
        if ( beanDemo != null) {
            String name = beanDemo.getName();
            Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
        }
        Bundle args = intent.getBundleExtra("BUNDLE");
        if ( args != null) {
            ArrayList<BeanDemo> object = (ArrayList<BeanDemo>) args.getSerializable("ARRAYLIST");
            String  name = object.get(1).getName();
            Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        InitBeanDemo();
        startActivityWithIntent(v);
    }

    private void startActivityWithIntent(View v) {
        if ( v == _button2) {
            Intent inty = new Intent(ShowSeriResultActivity.this,ShowSeriResultActivity.class); // send self
            Bundle bundle = new Bundle();
            bundle.putSerializable("key_one",beanDemo);
            inty.putExtras(bundle);
            startActivity(inty);
        }
        else if ( v == _button3) {
            ArrayList<BeanDemo> object = new ArrayList<BeanDemo>();
            Intent intent = new Intent(ShowSeriResultActivity.this, ShowSeriResultActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",array);
            intent.putExtra("BUNDLE",args);
            startActivity(intent);
        }
    }

    private void InitBeanDemo() {
        beanDemo = new BeanDemo();
        array = new ArrayList<BeanDemo>();
        array.add(new BeanDemo("name_one","age_1","add_1"));
        array.add(new BeanDemo("name_2","age_2","add_3"));
        array.add(new BeanDemo("name_3","age_3","add_3"));
        for (int i =0 ; i< array.size();i++){
            beanDemo.setName(array.get(i).getName());
            beanDemo.setAddress(array.get(i).getAddress());
            beanDemo.setAge(array.get(i).getAge());
        }
    }
}
