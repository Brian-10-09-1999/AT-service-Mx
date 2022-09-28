package com.example.devolucionmaterial.activitys.scrollDynamic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.devolucionmaterial.MainActivity;
import com.example.devolucionmaterial.R;
import com.example.devolucionmaterial.activitys.MenuInicial;
import com.example.devolucionmaterial.activitys.VistaMLeme;
import com.example.devolucionmaterial.activitys.scrollDynamic.network.ApiCallDragon;
import com.example.devolucionmaterial.data_base.BDVarGlo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ScrollDynamicActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;

    String host;



    private PreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_dynamic);




        prefManager = new PreferencesManager(this);

        if(!prefManager.isFirstTimeLaunch()){
            Log.d("SHARED NO", " preferences :"+prefManager.isFirstTimeLaunch());


        }else {

            Log.d("SHARED SI", " preferences :"+prefManager.isFirstTimeLaunch());
            RegisterDragon dragon = new RegisterDragon();
            dragon.execute();



        }


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        layouts = new int[]{
                R.layout.slide_scroll,
                R.layout.slide_scroll1,
                R.layout.slide_scroll2,
                R.layout.slide_scroll3,
                R.layout.slide_scroll4,
                R.layout.slide_scroll5,
                R.layout.slide_scroll6,
                R.layout.slide_scroll7,
                R.layout.slide_scroll8,
                R.layout.slide_scroll9,
                R.layout.slide_scroll10};


        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onBackPressed();
                //loadHomeActivity();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    onBackPressed();

                    //prefManager.setFirstTimeLaunch(false);
                    finish();

                }
            }
        });
    }

    public HttpUrl buildDragon(String tecnicoid) {
        return new HttpUrl.Builder()
                .scheme("http") //http
                .port(8082)
                .host(host)
                .addPathSegment("Android")//adds "/pathSegment" at the end of hostname
                .addPathSegments("atservice")
                .addPathSegments("fechaSlider.php")
                .addEncodedQueryParameter("idtecnico", tecnicoid) //add query parameters to the URL
                .build();
    }


    private class RegisterDragon extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String tecnicoid = BDVarGlo.getVarGlo(ScrollDynamicActivity.this, "INFO_USUARIO_ID");
            Log.d("dataid", " preferences : "+tecnicoid);
            String url = "";
            try {

                if (BDVarGlo.getVarGlo(ScrollDynamicActivity.this, "APP_PRUEBAS_o_PRODUCCION").equals("PRODUCCION")) {
                    host = "189.254.111.195";
                    Log.d("REST PRODUCCION", " url : "+url);
                }
                if (BDVarGlo.getVarGlo(ScrollDynamicActivity.this, "APP_PRUEBAS_o_PRODUCCION").equals("PRUEBAS")) {
                    url = BDVarGlo.getVarGlo(ScrollDynamicActivity.this, "URL_DOMINIO_PRUEBAS") + "Android/";
                    host = url.subSequence(7,27).toString();
                    Log.d("REST PRUEBAS", " url : "+host);
                }


                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();

                    String response = ApiCallDragon.GET(client, buildDragon(tecnicoid));
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.optInt("code");
                if(code==0){

                    prefManager.setFirstTimeLaunch(false);
                    Log.d("code : ", " code  "+ response);

                }else {
                    prefManager.setFirstTimeLaunch(true);

                }
            } catch (IOException e) {
                e.printStackTrace();
                prefManager.setFirstTimeLaunch(true);

            } catch (JSONException e) {
                e.printStackTrace();
                prefManager.setFirstTimeLaunch(true);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText("ENTENDIDO");
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText("SIGUIENTE");
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.vino_tool));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0){

        }
            dots[currentPage].setTextColor(Color.BLACK);
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
