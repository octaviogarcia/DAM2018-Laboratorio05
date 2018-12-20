package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * A simple {@link Fragment} subclass.
 */

public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {
    public static final int VER_MAPA = 0;
    public static final int OBTENER_COORDENADAS = 1;

    private GoogleMap miMapa;

    private int tipoMapa = 0;
    public MapaFragment() { }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        Bundle argumentos = getArguments();
        if(argumentos !=null) {
            tipoMapa = argumentos .getInt("tipo_mapa",0);
        }
        getMapAsync(this);

        return rootView;
    }
    @Override public void onMapReady(GoogleMap map) {
        miMapa = map;
        miMapa.getUiSettings().setZoomControlsEnabled(true);

        if(tipoMapa == OBTENER_COORDENADAS){
            miMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    listener.coordenadasSeleccionadas(latLng);
                }
            });
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
        
        miMapa.setMyLocationEnabled(true);

        ((MainActivity) getActivity()).obtenerLocation(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                terminarCarga(location);
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void terminarCarga(Location location){
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
        miMapa.setMyLocationEnabled(true);
        ((MainActivity) getActivity()).obtenerLocation(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                terminarCarga(location);
            }
        });
    }
}