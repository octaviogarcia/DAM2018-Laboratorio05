package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;


public class FormularioBusquedaFragment extends Fragment {
    public void setListener(OnFormularioBusquedaListener listener) {
        this.listener = listener;
    }

    public interface OnFormularioBusquedaListener {
        void mapaFormularioBusqueda(Reclamo.TipoReclamo tipoReclamo);
    }
    private OnFormularioBusquedaListener listener;

    public FormularioBusquedaFragment() {
        // Required empty public constructor
    }

    public static FormularioBusquedaFragment newInstance() {
        FormularioBusquedaFragment fragment = new FormularioBusquedaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    Button btBuscar;
    ArrayAdapter<Reclamo.TipoReclamo> tipoReclamoAdapter;
    Spinner spinnerTipoReclamo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_formulario_busqueda, container, false);
        spinnerTipoReclamo = v.findViewById(R.id.spinnerTipoReclamo);
        btBuscar = v.findViewById(R.id.btBuscar);

        tipoReclamoAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,Reclamo.TipoReclamo.values());
        tipoReclamoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoReclamo.setAdapter(tipoReclamoAdapter);
        btBuscar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reclamo.TipoReclamo tipoReclamo = (Reclamo.TipoReclamo) spinnerTipoReclamo.getSelectedItem();
                if(tipoReclamo == null) return;
                listener.mapaFormularioBusqueda(tipoReclamo);
            }
        });
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFormularioBusquedaListener) {
            listener = (OnFormularioBusquedaListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFormularioBusquedaListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
