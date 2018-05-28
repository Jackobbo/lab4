package com.example.jakubtomaszewski.lab4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> target;
    private SimpleCursorAdapter adapter;
    private MySQLite db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] values = new String[]{"q", "w", "e", "r", "t", "y"};


        this.db = new MySQLite(this);

        this.target = new ArrayList<String>();
        this.target.addAll(Arrays.asList(values));

        this.adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                db.lista(),
                new String[] {"_id", "gatunek"},
                new int[] {android.R.id.text1, android.R.id.text2},
                SimpleCursorAdapter.IGNORE_ITEM_VIEW_TYPE
        );

        ListView listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(this.adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id)
            {
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                Animal zwierz = db.pobierz(Integer.parseInt(name.getText().toString()));

                Intent intencja = new Intent(getApplicationContext(), DodajWpis.class);
                intencja.putExtra("element", zwierz);
                startActivityForResult(intencja, 2);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView) view.findViewById(android.R.id.text1);
                Animal zwierz = db.pobierz(Integer.parseInt(name.getText().toString()));

                db.usun(String.valueOf(zwierz.get_id()));
                adapter.changeCursor(db.lista());
                adapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        if(reqCode > 0 && resCode==RESULT_OK) {
            Bundle extras = data.getExtras();

            if (reqCode == 1) {
                Animal nowy = (Animal) extras.get("nowy");
                this.db.dodaj(nowy);
            }

            else if (reqCode == 2) {
                Animal edytowany = (Animal) extras.get("nowy");
                this.db.aktualizuj(edytowany);
            }

            adapter.changeCursor(db.lista());
            adapter.notifyDataSetChanged();
        }
    }

    public void nowyWpis(MenuItem mi){
        Intent intencja = new Intent(this, DodajWpis.class);
        startActivityForResult(intencja, 1);
    }
}