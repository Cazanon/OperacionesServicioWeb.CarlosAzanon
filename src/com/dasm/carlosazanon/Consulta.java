package com.dasm.carlosazanon;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Consulta extends Activity {

	String respuesta;
	JSONArray registros;
	int numRegistros;
	int registroActual;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_consulta);
	
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		String dni = extras.getString("dni");		
		new ConsultaBD().execute(dni);
		//String mensaje = getIntent().getStringExtra("dato1");		
		//Toast.makeText(Consulta.this, mensaje, Toast.LENGTH_SHORT).show();
		
		//((EditText)findViewById(R.id.dniC)).setText(dni);	
		String mensaje="";
		if(dni.equals("") && numRegistros==0){
			mensaje="No existe el DNI";
		}else if(numRegistros==0){
			mensaje="No hay registros para mostrar";			
		}else{
			try {
				numRegistros = registros.getJSONObject(0).getInt("NUMREG");
				registroActual=1;
				mostrarRegistro(registroActual);
			} catch (JSONException e) {				
				e.printStackTrace();
			}
		}
		 
	}
	
	/*
	public void responderActividadUno(View v){
		//Intent i=new Intent();
		//i.putExtra("respuesta","OK");
		//setResult(RESULT_OK,i);
		finish();
	}
	*/
	@Override
	public void onBackPressed(){
		//Intent i=new Intent();
		//i.putExtra("respuesta","Pulsado para Atras!");
		//setResult(RESULT_OK,i);
		super.onBackPressed();
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
					pDialog = new ProgressDialog(Consulta.this);
					pDialog.setMessage(getString(R.string.progress_title));
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(true);
					pDialog.show();
				}
				
				@Override
				protected String doInBackground(String... parametros) {
					String dni = parametros[0];
					String respuesta = "";
					String url_final = URL;
					if(!dni.equals("")){
						url_final+="/"+dni;
					}
					try {
						AndroidHttpClient httpClient = AndroidHttpClient.newInstance("AndroidHttpClient");
						HttpGet httpGet = new HttpGet(url_final);
						HttpResponse response = httpClient.execute(httpGet);
						respuesta = EntityUtils.toString(response.getEntity());
						httpClient.close();
					} catch (IOException e) {
						error=true;
						Log.e("Error en la operacion",e.toString());
					}
					return respuesta;			
				}
				
				@Override
				protected void onPostExecute(String respuesta) {
					pDialog.dismiss();
					String mensaje = "";
					if(error){
						mensaje = "La consulta genera un error";
						Toast.makeText(Consulta.this, mensaje, Toast.LENGTH_SHORT).show();
						return;
					}
					try {
						registros = new JSONArray(respuesta);
						numRegistros = registros.getJSONObject(0).getInt("NUMREG");
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
					//Toast.makeText(Consulta.this, mensaje, Toast.LENGTH_SHORT).show();				
				}	    	
	 
	 }
	 
	 private void mostrarRegistro(int numeroRegistro){
			try {
				TextView textIdentificadorRegistro = (TextView)findViewById(R.id.nRegistros);
				textIdentificadorRegistro.setText("Registro "+numeroRegistro+" de "+numRegistros);

				JSONObject registroMostrable = registros.getJSONObject(numeroRegistro);

				TextView txtDni = (TextView)findViewById(R.id.dniC);
				TextView txtNombre = (TextView)findViewById(R.id.nombreC);
				TextView txtApellidos = (TextView)findViewById(R.id.apellidoC);
				TextView txtDireccion = (TextView)findViewById(R.id.direccionC);
				TextView txtTelefono = (TextView)findViewById(R.id.telefonoC);
				TextView txtEquipo = (TextView)findViewById(R.id.equipoC);

				txtNombre.setText(registroMostrable.getString("nombreC"));
				txtNombre.setFocusable(false);
				txtApellidos.setText(registroMostrable.getString("apellidoC"));
				txtApellidos.setFocusable(false);
				txtDireccion.setText(registroMostrable.getString("direccionC"));
				txtDireccion.setFocusable(false);
				txtTelefono.setText(registroMostrable.getString("telefonoC"));
				txtTelefono.setFocusable(false);
				txtEquipo.setText(registroMostrable.getString("equipoC"));
				txtEquipo.setFocusable(false);				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	
}
