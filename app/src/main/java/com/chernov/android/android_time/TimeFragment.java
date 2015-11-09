package com.chernov.android.android_time;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chernov.android.android_time.DataBase.Simple;
import com.chernov.android.android_time.DataBase.TimeDatabase;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeFragment extends ListFragment implements View.OnClickListener {

    // Reference of DatabaseHelper class to access its DAOs and other components
    private TimeDatabase databaseHelper = null;
    private Button btnFon, btnData;
    private RelativeLayout layout;
    private TextView textView;
    private RuntimeExceptionDao<Simple, Integer> simpleDao;
    private ArrayAdapter<String> adapter;
    private List<String> items = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // при смене ориентации экрана фрагмент сохраняет свое состояние. onDestroy не вызывается
        setRetainInstance(true);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instance) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        components(view);

        if(instance == null) {
            // This is how, DatabaseHelper can be initialized for future use
            getHelper();
            // load all items in listview
            readDB();
        }

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState!=null) {
            layout.setBackgroundColor(savedInstanceState.getInt("sColor"));
            textView.setText(savedInstanceState.getString("tInfo"));
        }
    }

    private void components(View view) {
        btnFon = (Button) view.findViewById(R.id.fon);
        btnFon.setOnClickListener(this);
        btnData = (Button) view.findViewById(R.id.data);
        btnData.setOnClickListener(this);
        layout = (RelativeLayout) view.findViewById(R.id.layout);
        textView = (TextView) view.findViewById(R.id.info);
    }

    // This is how, DatabaseHelper can be initialized for future use
    private void getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), TimeDatabase.class);
            simpleDao = databaseHelper.getSimpleDao();
        }
    }

    // load all items in listview
    private void readDB() {
       // запросить все объекты в БД
       List<Simple> list = simpleDao.queryForAll();
       // если симплы существуют в БД
       for (Simple simple : list) {
            items.add(simple.toString());
       }
       adapter.notifyDataSetChanged();
    }

    private void addNewItem(String item) {
        // store it in the database
        simpleDao.create(new Simple(item));
        adapter.notifyDataSetChanged();
    }

    // общение между потоками через ResultReceiver
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 1) {
                String item = resultData.getString(TimeIntentService.ITEM);
                // добавляем в listview
                items.add(item);
                // добавляем в БД
                addNewItem(item);
                // перемещаемся на самый нижний элемент
                getListView().smoothScrollToPosition(items.size());
                textView.setText(item);
            } else {
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
            }
            btnData.setEnabled(true);
        };
    };

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.fon:
                changeBackground();
                break;
            case R.id.data:
                btnData.setEnabled(false);
                startService();
                break;
        }
    }

    private void changeBackground() {
        layout.setBackgroundColor(-new Random().nextInt(0xFFFFFF));
    }

    // запускаем сервис, парсим страницу
    private void startService() {
        Intent intent = new Intent(getActivity(), TimeIntentService.class);
        intent.putExtra(TimeIntentService.RECEIVER, resultReceiver);
        getActivity().startService(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("sColor", ((ColorDrawable)layout.getBackground()).getColor());
        outState.putString("tInfo", textView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*
		 * You'll need this in your class to release the helper when done.
		 */
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        // если сервис не остановлен, останавливаем его
        if(!TimeIntentService.isStopped) {
            getActivity().stopService(new Intent(getActivity(), TimeIntentService.class));
        }
    }
}

