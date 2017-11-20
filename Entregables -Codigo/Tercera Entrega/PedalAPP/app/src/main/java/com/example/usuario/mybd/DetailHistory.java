package com.example.usuario.mybd;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DetailHistory extends AppCompatActivity   implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker mSydney;
    private Marker mDestino;
    private TextView txtDetalle ;
    private TextView txtDistancia ;
    private TextView txtDuracion ;
    private TextView txtFecha ;
    private TextView txtHora;
    private TextView txtClima;

    private WebView graficoElevacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        txtDetalle=(TextView)findViewById(R.id.txtDetalle);
        txtDistancia=(TextView)findViewById(R.id.txtDetalleDistancia);
        txtDuracion=(TextView)findViewById(R.id.txtDetalleDuracion);
        txtFecha=(TextView)findViewById(R.id.txtDetalleFecha);
        txtHora=(TextView)findViewById(R.id.txtDetalleHora);
        txtClima=(TextView)findViewById(R.id.txtDetalleClima);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        Ruta objeto = (Ruta) bundle.getSerializable("ruta");
        ruta = objeto;
        txtDetalle.setText(bundle.getInt("position")+"");
        txtDistancia.setText(ruta.getKilometros()+"");
        String h= ruta.getHoras() < 10  ? "0"+ruta.getHoras()+"" : ruta.getHoras()+"";
        String m= ruta.getMinutos() < 10  ? "0"+ruta.getMinutos()+"" : ruta.getMinutos()+"";
        txtDuracion.setText(h+":"+m);
        SimpleDateFormat formatFecha=new SimpleDateFormat("dd/MM/yyyy");
        txtFecha.setText(formatFecha.format(ruta.getFecha()));
        SimpleDateFormat formatHora=new SimpleDateFormat("hh:mm:ss");
        txtHora.setText(formatHora.format(ruta.getFecha()));
        txtClima.setText(ruta.getClima());

        graficoElevacion = (WebView) findViewById(R.id.elevationGraph);


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapRuta);
        mapFragment.getMapAsync(this);

    }

    Ruta ruta;//=new Ruta();
    Boolean modo = true;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        //4.598156094243696
        //-74.07604694366455
        LatLng sydney = new LatLng(ruta.getLatitudIncial(), ruta.getLongitudInicial());
        Date d = ruta.getFecha();
        DateFormat format = new SimpleDateFormat("HH");
        int h = Integer.parseInt(format.format(d).toString());
        if (h >= 18 || h < 6) {
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.nochevacia)
            );
            mSydney = mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("Posicion")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicicleta)));
            modo = false;
        } else {
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.diavacia)
            );
            mSydney = mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("Posicion")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicicleta2)));

        }


        mDestino = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(ruta.getLatitudFinal(), ruta.getLongitudFinal()))
                .title("Destino")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bandera)));
        mDestino.setVisible(true);

        //mSydney.setTag(0);

        //  mMap.addMarker(mSydney);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        pintarRuta();
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    void pintarRuta() {

        LatLng origin = mSydney.getPosition();

        LatLng dest = mDestino.getPosition();

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", "URL:" + url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);

    }
    class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadRouteUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data

            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }



        PolylineOptions lineOptions = null;

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            if (lineOptions != null) {
                lineOptions = null;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }


                //SOLICITUD ALTURA
                String urlElevation = "http://maps.googleapis.com/maps/api/elevation/json?locations=";
                for(int j = 0; j < points.size(); j++){

                    urlElevation += points.get(j).latitude+","+points.get(j).longitude;
                    if(j != points.size() - 1){
                        urlElevation += "|";
                    }

                }
                Log.d("onPostExecute", "URL altura " + urlElevation);


                FetchElevationUrl fetchElevationUrl = new FetchElevationUrl();
                fetchElevationUrl.execute(urlElevation);


                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                mMap.addPolyline(lineOptions);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
        }
    }

    private String downloadRouteUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String downloadElevationUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    class FetchElevationUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadElevationUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskElevation parserTaskElevation = new ParserTaskElevation();

            // Invokes the thread for parsing the JSON data

            parserTaskElevation.execute(result);

        }
    }

    private class ParserTaskElevation extends AsyncTask< String, Integer, List<Double> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<Double> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<Double> elevaciones = null;

            try {
                elevaciones = new ArrayList<Double>();
                jObject = new JSONObject(jsonData[0]);

                JSONArray arr = jObject.getJSONArray("results");
                for(int i = 0; i < arr.length(); ++i){
                    JSONObject punto = arr.getJSONObject(i);

                    double elevation = punto.getDouble("elevation");

                    elevaciones.add(elevation);
                }

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return elevaciones;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<Double> result) {

            if(result.size() > 0){
                String graphUrl = "http://chart.googleapis.com/chart?";

                String chartType = "cht=" + "lc";
                String chartSize = "chs=" + "500x160";
                String chartLabel = "chl=" + "Elevación de la ruta (en metros)";
                String chartColor = "chco=" + "orange";

                //String chartDataScaling = "chds=" + "-500,5000";
                int minRange = (int) (result.get(0)-100);
                int maxRange = (int) (result.get(0)+100);
                String chartDataScaling = "chds=" + minRange +","+ maxRange;

                String chartVisibleAxes = "chxt=" + "x,y";
                //String chartAxisRange = "chxr=" + "1,-500,5000";
                String chartAxisRange = "chxr=" + "1," + minRange + "," + maxRange;

                String datos = "chd=" + "t:";
                for(int i = 0; i < result.size(); ++i){
                    datos += result.get(i);

                    if(i != result.size()-1){
                        datos += ",";
                    }
                }

                graphUrl += chartType + "&" + chartSize + "&" + chartLabel + "&" + chartColor + "&"
                        + chartDataScaling + "&" + chartVisibleAxes + "&"
                        + chartAxisRange + "&" + datos;

                Log.d("ParserTaskElevation", "onPostExecute: graphUrl = " + graphUrl);


                graficoElevacion.loadUrl(graphUrl);
            }else{
                Toast.makeText(getBaseContext(),"Error cargando la gráfica",Toast.LENGTH_SHORT).show();
            }


        }
    }


}
