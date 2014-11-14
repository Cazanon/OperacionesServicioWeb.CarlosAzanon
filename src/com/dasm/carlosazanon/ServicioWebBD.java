package com.dasm.carlosazanon;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ServicioWebBD extends Activity {

	private EditText dni;
	private final int acti=2;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dni = (EditText)findViewById(R.id.dni);
    }
    
    public void consultar(View v){
    	new ConsultaBD().execute(dni.getText().toString());
    }
    
    public void lanzarActividad2(View v){
    	Intent i = new Intent(this,Actividad2.class);
    	i.putExtra("dato1", dni.getText().toString());
    	startActivityForResult(i,acti);
    }
    
    @Override
    public void onActivityResult(int actividad,int resultado,Intent datos){
    	if(actividad==acti){
    		String respuesta=datos.getStringExtra("respuesta");
    		Toast.makeText(ServicioWebBD.this, "Vuelvo de la actividad 2, respuesta:"+respuesta, Toast.LENGTH_SHORT).show();
    	}else{
    		Toast.makeText(ServicioWebBD.this, "Resultado erroneo", Toast.LENGTH_SHORT).show();
    	}
    	
    }
    
    private class ConsultaBD extends AsyncTask<String,Void,String>{
	//Parametro de la entrada
	//Parametro que manda a la interfaz (por ejemplo el progreso)
	//Parametro que devuelve el doInBackground y recibe el post execute
    	
    	//POST -> Inserta registro nuevo
    	//PUT  -> Actualizar registro

    	private ProgressDialog pDialog;
    	private boolean error;
    	private final String URL = "http://demo.calamar.eui.upm.es/dasmapi/v1/miw04/fichas";
    	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			error=false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")){
				url_final+="/"+dni;
			}
			try {
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpGet = new HttpGet(url_final);
				HttpResponse response = httpClient.execute(httpGet);
				datos = EntityUtils.toString(response.getEntity());
				httpClient.close();
			} catch (IOException e) {
				error=true;
				Log.e("Error en la operacion",e.toString());
			}
			return datos;			
		}
		
		@Override
		protected void onPostExecute(String info) {
			pDialog.dismiss();
			String mensaje = "";
			if(error){
				mensaje = "La consulta genera un error";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(info);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros){
					case -1: mensaje = "La consulta genera un error";
						break;
					case 0: mensaje = "La consulta no devuelve registros";
						break;
					default: mensaje = "La consulta devuelve "+numRegistros+" registro/s";					
				}				
			} catch (JSONException e) {
				Log.e("Error en la conversion",e.toString());
			}
			Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
		}
    	
    }
}
