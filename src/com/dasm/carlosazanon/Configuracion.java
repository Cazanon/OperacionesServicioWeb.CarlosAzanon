package com.dasm.carlosazanon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class Configuracion extends PreferenceActivity {
	
	private String URL;
	private String usuario;
	private String pass;
	
    @Override
    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new Fragment()).commit();
		Bundle extras = getIntent().getExtras();		
		URL=extras.getString("url");
		SharedPreferences conf=PreferenceManager.getDefaultSharedPreferences(Configuracion.this);
		conf.getString("direccion", URL);
		conf.getString("usuario", "miw04");
		conf.getString("pass", "139285330");
    }

    public static class Fragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.configuracion);
        }
    }
    
	@Override
	public void onBackPressed(){
		obtenerConfiguracion();
		Intent i=new Intent();
		i.putExtra("url",URL);
		i.putExtra("usuario",usuario);
		i.putExtra("pass",pass);
		setResult(RESULT_CANCELED,i);
		super.onBackPressed();
	}
	
	public void obtenerConfiguracion(){
		SharedPreferences conf=PreferenceManager.getDefaultSharedPreferences(Configuracion.this);
		URL=conf.getString("direccion","");
		usuario=conf.getString("usuario","");
		pass=conf.getString("pass","");
	}
	
}
