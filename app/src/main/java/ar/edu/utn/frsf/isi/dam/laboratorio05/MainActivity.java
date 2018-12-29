package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;


public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener,
        NuevoReclamoFragment.OnNuevoLugarListener,
        MapaFragment.OnMapaFragmentListener,
        ListaReclamosFragment.OnListaReclamosListener,
        FormularioBusquedaFragment.OnFormularioBusquedaListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private FusedLocationProviderClient mFusedLocationClient;




    public void obtenerLocation(OnSuccessListener<Location> callback) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.navview);
        BienvenidoFragment fragmentInicio = new BienvenidoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragmentInicio)
                .commit();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        boolean fragmentTransaction = false;
                        Fragment fragment = null;
                        String tag = "";
                        switch (menuItem.getItemId()) {
                            case R.id.optNuevoReclamo: {
                                tag = "nuevoReclamoFragment";
                                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null) {
                                    fragment = new NuevoReclamoFragment();
                                    ((NuevoReclamoFragment) fragment).setListener(MainActivity.this);
                                }

                                fragmentTransaction = true;
                            }break;
                            case R.id.optListaReclamo: {
                                tag = "listaReclamos";
                                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null) {
                                    fragment = new ListaReclamosFragment();
                                    ((ListaReclamosFragment) fragment).setListener(MainActivity.this);
                                }
                                fragmentTransaction = true;
                            }break;
                            case R.id.optVerMapa: {
                                tag = "mapaReclamos";
                                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null) {
                                    fragment = new MapaFragment();
                                    ((MapaFragment) fragment).setListener(MainActivity.this);
                                }
                                Bundle b = (new Bundle());
                                b.putInt("tipo_mapa", MapaFragment.MOSTRAR_RECLAMOS);
                                fragment.setArguments(b);
                                fragmentTransaction = true;
                            }break;
                            case R.id.optHeatMap: {
                                tag = "mapaReclamos";
                                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null) {
                                    fragment = new MapaFragment();
                                    ((MapaFragment) fragment).setListener(MainActivity.this);
                                }
                                Bundle b = (new Bundle());
                                b.putInt("tipo_mapa", MapaFragment.MOSTRAR_HEATMAP);
                                fragment.setArguments(b);
                                fragmentTransaction = true;
                            }break;
                            case R.id.optFormularioBusqueda: {
                                tag = "formularioBusqueda";
                                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                                if (fragment == null) {
                                    fragment = new FormularioBusquedaFragment();
                                    ((FormularioBusquedaFragment) fragment).setListener(MainActivity.this);
                                }
                                fragmentTransaction = true;
                            }break;
                        }

                        if(fragmentTransaction) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.contenido, fragment,tag)
                                    .addToBackStack(null)
                                    .commit();

                            menuItem.setChecked(true);

                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public void coordenadasSeleccionadas(LatLng c) {
            String tag = "nuevoReclamoFragment";
            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tag);
            if(fragment==null) {
                fragment = new NuevoReclamoFragment();
                ((NuevoReclamoFragment) fragment).setListener(MainActivity.this);
            }

            Bundle bundle = new Bundle();
            bundle.putString("latLng",c.latitude+";"+c.longitude);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenido, fragment,tag)
                    .commit();
    }

    @Override
    public void obtenerCoordenadas() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("mapaReclamos");
        //TODO si "fragment" es null entonces crear el fragmento mapa, agregar un bundel con el parametro tipo_mapa
        if(fragment==null) {
            fragment = new MapaFragment();
            ((MapaFragment) fragment).setListener(MainActivity.this);
        }

        Bundle b = (new Bundle());
        b.putInt("tipo_mapa",MapaFragment.OBTENER_COORDENADAS);
        fragment.setArguments(b);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, fragment,"mapaReclamos")
                .commit();
    }

    public void mostrarReclamo(int id){
        Fragment f = getSupportFragmentManager().findFragmentByTag("mapaReclamos");
        if(f==null) {
            f = new MapaFragment();
            ((MapaFragment) f).setListener(this);
        }
        Bundle args = new Bundle();
        args.putInt("tipo_mapa",MapaFragment.MOSTRAR_RECLAMO);
        args.putInt("idReclamo",id);
        f.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, f)
                .addToBackStack(null)
                .commit();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        getSupportFragmentManager().getFragments().get(0).onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    @Override
    public void mapaFormularioBusqueda(Reclamo.TipoReclamo tipoReclamo) {
        Fragment f = getSupportFragmentManager().findFragmentByTag("mapaReclamos");
        if(f==null) {
            f = new MapaFragment();
            ((MapaFragment) f).setListener(this);
        }
        Bundle args = new Bundle();
        args.putInt("tipo_mapa",MapaFragment.MOSTRAR_BUSQUEDA);
        args.putString("tipo_reclamo",tipoReclamo.toString());
        f.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, f)
                .addToBackStack(null)
                .commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
    }
}
