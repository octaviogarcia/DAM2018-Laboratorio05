package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.ReclamoDao;

/**
 * A simple {@link Fragment} subclass.
 */

public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {
    public static final int VER_MAPA = 0;
    public static final int OBTENER_COORDENADAS = 1;
    public static final int MOSTRAR_RECLAMOS = 2;
    public static final int MOSTRAR_RECLAMO = 3;

    private GoogleMap miMapa;
    private ReclamoDao reclamoDao;
    private int tipoMapa = 0;
    private int idReclamo = -1;
    public MapaFragment() { }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        Bundle argumentos = getArguments();
        if(argumentos !=null) {
            tipoMapa = argumentos.getInt("tipo_mapa",0);
            idReclamo = argumentos.getInt("idReclamo",-1);
        }
        reclamoDao = MyDatabase.getInstance(getActivity()).getReclamoDao();
        getMapAsync(this);

        return rootView;
    }

    void handleObtenerCoordenadas(){
        miMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                listener.coordenadasSeleccionadas(latLng);
            }
        });
    }
    void handleMostrarReclamos(){
        Runnable setearMarkers = new Runnable() {
            @Override
            public void run() {
                final List<Reclamo> reclamos = reclamoDao.getAll();
                if(reclamos.isEmpty()) return;
                double minLat = Double.POSITIVE_INFINITY;
                double minLong = Double.POSITIVE_INFINITY;
                double maxLat = Double.NEGATIVE_INFINITY;
                double maxLong = Double.NEGATIVE_INFINITY;
                for(Reclamo r : reclamos) {
                    double lat = r.getLatitud();
                    double lon = r.getLongitud();
                    if (lat > maxLat) maxLat = lat;
                    if (lat < minLat) minLat = lat;
                    if (lon > maxLong) maxLong = lon;
                    if (lon < minLong) minLong = lon;
                }

                LatLng min = new LatLng(minLat,minLong);
                LatLng max = new LatLng(maxLat,maxLong);
                final LatLngBounds limite = new LatLngBounds(min,max);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        miMapa.moveCamera(CameraUpdateFactory.newLatLngBounds(limite,100));
                        for(Reclamo r : reclamos){
                            LatLng latLng = new LatLng(r.getLatitud(),r.getLongitud());
                            miMapa.addMarker(new MarkerOptions().position(latLng).title(r.getReclamo()));
                        }
                    }
                });
            }
        };
        Thread thread = new Thread(setearMarkers);
        thread.start();
    }

    void handleMostrarReclamo(){
        Runnable setearMarkers = new Runnable() {
            @Override
            public void run() {
                final Reclamo reclamo = reclamoDao.getById(idReclamo);
                if(reclamo == null) return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LatLng latLng = new LatLng(reclamo.getLatitud(),reclamo.getLongitud());
                        miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        miMapa.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(reclamo.getReclamo()));
                        miMapa.addCircle(new CircleOptions()
                                .center(latLng)
                                .radius(500)
                                .fillColor(Color.parseColor("#32FF0000"))
                                .strokeColor(Color.RED));
                    }
                });
            }
        };
        Thread thread = new Thread(setearMarkers);
        thread.start();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        miMapa = map;
        miMapa.getUiSettings().setZoomControlsEnabled(true);

        if(tipoMapa == OBTENER_COORDENADAS){
            handleObtenerCoordenadas();
        }
        else if(tipoMapa == MOSTRAR_RECLAMOS){
            handleMostrarReclamos();
        }
        else if(tipoMapa == MOSTRAR_RECLAMO){
            handleMostrarReclamo();
        }

        if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        terminarCarga();


    }

    @SuppressLint("MissingPermission")
    private void terminarCarga(){
        miMapa.setMyLocationEnabled(true);

        //Si estoy mostrando reclamos no lo muevo a la locacion actual
        if(tipoMapa != VER_MAPA) return;

        ((MainActivity) getActivity()).obtenerLocation(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                moveLocation(location);
            }
        });
    }

    private void moveLocation(Location location){
        LatLng pos = new LatLng(location.getLatitude(),location.getLongitude());
        miMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,15));
    }

    public interface MapaFragmentListener {
        public void coordenadasSeleccionadas(LatLng c);
    }

    public MapaFragmentListener listener;
    public void setListener(MapaFragmentListener mainActivity) {
        listener = mainActivity;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i = 0;i<grantResults.length;i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Log.d("PERMISOS", "Fallo permiso " + permissions[i]);
                return;
            } else {
                Log.d("PERMISOS", "Permitido " + permissions[i]);
            }
        }

        terminarCarga();
    }
}