package com.example.daniele.artreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    Lists myLists = new Lists();
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                {
                    loadListView("history");
                }
                if (position == 2)
                {
                    loadListView("favourites");
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(1);
        //setContentView(R.layout.fragment_main);
        /*int screenWidth = getScreenWidthInDPs(getApplicationContext());
        Button btnScan = (Button)findViewById(R.id.btnQrScan);
        btnScan.setWidth(screenWidth);*/


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        loadStructures();

    }

    //SOLO AL LANCIO leggo i file di testo, poi lavoro sulle struttutre!!!
    private void loadStructures()
    {
        myLists.listArtworksHistory.clear();
        myLists.listArtworksFavourites.clear();
        //cronologia
        String aux = readFromFile("history.txt");
        if (aux != "")
        {
            String righe[] = aux.split(";");
            String[] colonne;
            for (int i = 0; i < righe.length; i++) {
                colonne = righe[i].split("-");
                myLists.addHistoryRecord((new Artwork(Integer.parseInt(colonne[0]), colonne[1], colonne[2], colonne[3])));
            }
        }

        //preferiti
        String aux1 = readFromFile("favourites.txt");
        if (aux1 != "")
        {
            String righe1[] = aux1.split(";");
            String[] colonne1;
            for (int i = 0; i < righe1.length; i++) {
                colonne1 = righe1[i].split("-");
                myLists.addFavouriteRecord((new Artwork(Integer.parseInt(colonne1[0]), colonne1[1], colonne1[2], colonne1[3])));
            }
        }

    }

    /*****************
     //  GESTIONE DEI FILE CRONOLOGIA E PREFERITI
     /*****************/
    private void writeToFile(String data, String filename)
    {
        try {
            Context context = getApplicationContext();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));

            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());

        }
    }

    private String readFromFile(String filename) {
        String ret = "";
        try
        {
            InputStream inputStream = openFileInput(filename);

            if ( inputStream != null )
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
            //CREO IL FILE
            else
            {
                writeToFile("", filename);
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e)
        {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    /***************FINE GESTIONE FILE TXT**************/




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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int nPage = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;

            //main
            if (nPage == 2)
            {
                rootView = inflater.inflate(R.layout.fragment_main, container, false);

            }

            //history
            else if (nPage == 1)
            {
                rootView = inflater.inflate(R.layout.fragment_history, container, false);
                //loadListView("history");
            }

            //favourite
            else if (nPage == 3)
            {
                rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
                //loadListView("favourites");
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
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

    public void scanQR(View v) {
        try
        {
             Intent intent = new Intent(ACTION_SCAN);
             intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
             startActivityForResult(intent, 0);

            //testPopolaInfoArtwork(); //old
           /* String strJson = "{\"id\":8,\"title\":\"La Gioconda\",\"author\":1,\"abstract\":\"placeholder\",\"pictureUrl\":\"http://1.bp.blogspot.com/-mAlupcNJ_A8/TudH7XRbAEI/AAAAAAAAAco/Cn8Z8lKDYSM/s1600/Leonardo+La+Gioconda+1503+1506.jpg\",\"tecnique\":\"Olio su tavola\",\"year\":1506,\"artMovement\":\"Rinascimento\",\"dimensionHeight\":77,\"dimensionWidth\":53,\"wikipediaPageArtwork\":\"https://it.wikipedia.org/wiki/Gioconda\",\"location\":1,\"pictureUrl2\":\"Artwork8\",\"pictureUrl3\":\"Location1\",\"idLocationsArtworks\":1,\"description\":\"Louvre\",\"city\":\"Parigi\",\"nation\":\"France\",\"wikipediaPageLocation\":\"https://it.wikipedia.org/wiki/Museo_del_Louvre\",\"address\":\"Musée du Louvre, 75001 Paris, France\",\"idAuthor\":1,\"name\":\"Leonardo Da Vinci\n\",\"wikipediaPageAuthor\":\"https://it.wikipedia.org/wiki/Leonardo_da_Vinci\",\"nationalityAuthor\":\"Italia\"}";


            //richiamo seconda intent passandogli come parametro la stringa json letta
            intent = new Intent(this, ScrollingActivity.class);
            //intent.putExtra("listFavourites", myLists);
            intent.putExtra("jsonArtwork", strJson);
            intent.putExtra("jsonHistory", myLists.historyToString());
            intent.putExtra("jsonFavourites", myLists.favouritesToString());
            startActivity(intent);

            Snackbar.make(v, "Aggiornamento in corso ...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            loadStructures(); //trovare un modo per eliminare la ri lettura dei file txt
            loadListView("favourites");
            loadListView("history");*/
        }
        catch (ActivityNotFoundException anfe) {
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }

        //InviaRichiestaHttp http = new InviaRichiestaHttp();
        //http.execute("get", "/allArtworks");

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                //in content c'è il contenuto del QrCode:
                //es: ArtworkId/Id
                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);

                String[] res = contents.split("/");
                String id = res[1];

                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!SOSTITUIRE CON API
                //chiamare api oneArtwork
                //
                // toast = Toast.makeText(this, id, Toast.LENGTH_LONG);
                // toast.show();
            }
        }
    }
    /*****************FINE PULSANTE SCAN*****************/

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    /*************
     * Popolo i due listview CRONOLOGIA e PREFERITI
     * @param lstType
     */
    private void loadListView(String lstType)
    {
        ListView lstArt;
        if (lstType == "history")
        {
          //  setContentView(R.layout.fragment_history);


            RecordAdapter adapter = new RecordAdapter(this, R.layout.record_layout, myLists.listArtworksHistory);
            lstArt = (ListView) findViewById(R.id.listViewHistory);
            lstArt.setAdapter(null);
            lstArt.setAdapter(adapter);
            lstArt.setOnItemClickListener(listener);
        }

        else if (lstType == "favourites")
        {
            //setContentView(R.layout.fragment_favourite);

            RecordAdapter adapter = new RecordAdapter(this, R.layout.record_layout, myLists.listArtworksFavourites);
            lstArt = (ListView) findViewById(R.id.listViewFavourite);
            lstArt.setAdapter(null);
            lstArt.setAdapter(adapter);
            lstArt.setOnItemClickListener(listener);
        }
    }

    ListView.OnItemClickListener listener = new ListView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l)
        {
            //Artwork item = (Artwork) adapterView.getItemAtPosition(pos);

            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!SOSTITUIRE CON API
            //Intent intent = new Intent(this, ScrollingActivity.class);
            //startActivity(intent);
        }
    };
}