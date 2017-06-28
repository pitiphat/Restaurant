package com.example.guide.restaurant;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ItemShopActivity extends AppCompatActivity {


    private ListView listview_items;
    private LisViewAdapter listAdapter;
    private ItemShopResult itemShopResult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_shop);


        //get tableID from previos activity
        Bundle bundle = getIntent().getExtras();
        String tableID = bundle.getString("tableID");


        Log.d("TableID", "TableID : " + tableID);

        new FeedTask().execute(getString(R.string.api_1));

        listview_items = (ListView) findViewById(R.id.itemListview);
        listAdapter = new LisViewAdapter();
        listview_items.setAdapter(listAdapter);

    }

    public class FeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url(params[0])
                    .build();

            try {
                Response result = client.newCall(request).execute();
                return result.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            Log.d("ItemList", "ItemList : " + s);
            Gson gson = new Gson();
            itemShopResult = gson.fromJson(s, ItemShopResult.class);
            listAdapter.notifyDataSetChanged();

        }
    }

    private class LisViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (itemShopResult == null) {
                return 0;
            } else {
//                Log.d("feedLog", "Package Name : " );
                return itemShopResult.getData().size();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Viewholder holder = null;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.custom_listview_item, null);
                holder = new Viewholder();
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                holder.txt_description = (TextView) convertView.findViewById(R.id.txt_description);
                holder.txt_price = (TextView) convertView.findViewById(R.id.txt_price);
                holder.item_images = (ImageView) convertView.findViewById(R.id.img_item);

                holder.chooseItem = (Button) convertView.findViewById(R.id.ChooseItem);
                holder.chooseItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String itemId = (String) v.getTag(R.id.ChooseItem); //index ของ array

                        Log.d("CustomLog", "Choose iTem : " + itemId);


                        showDialogAmount();
                    }
                });

                convertView.setTag(holder);

            } else {
                holder = (Viewholder) convertView.getTag();
            }


            holder.item_images.setTag(R.id.img_item, position);
            ItemShopResult.DataBean item = itemShopResult.getData().get(position);
            holder.txt_name.setText(item.getName());

            String imageURL = getString(R.string.api_2) + "/shop-slim/img_items/" + item.getImg() + "?dummy=3";

//            Log.d("imageURL", "imageURL : " + imageURL);
            Glide.with(getApplicationContext()).load(imageURL).into(holder.item_images);

            holder.txt_price.setText(item.getPrice() + " Baht ");

            // holder.chooseItem.setTag(R.id.ChooseItem, position); //ส่งตำแหน่ง ของ array เข้าไป
            holder.chooseItem.setTag(R.id.ChooseItem, item.getId());

            return convertView;
        }
    }

    public class Viewholder {
        ImageView item_images;
        TextView txt_name;
        TextView txt_price;
        TextView txt_description;
        Button chooseItem;
    }

    public void showDialogAmount() {


        final Dialog d = new Dialog(ItemShopActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.custom_dialog_item);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);


        Spinner dropdown = (Spinner) d.findViewById(R.id.CSD_amountSpin);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(dropdown);

            // Set popupWindow height to 500px
            popupWindow.setHeight(300);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        String[] items = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("ItemID", "ItemID : " + itemId);
//                Log.d("feedLog", "Package Name : " + TableId);
//                String amount = String.valueOf(np.getValue());
//                Toast.makeText(getApplicationContext(),String.valueOf(np.getValue()), Toast.LENGTH_LONG).show();

//                new Gateway().execute(getString(R.string.api_2)+"/shop-slim/api/order", String.valueOf(TableId), String.valueOf(itemId), amount);
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });


        d.show();


    }


}

