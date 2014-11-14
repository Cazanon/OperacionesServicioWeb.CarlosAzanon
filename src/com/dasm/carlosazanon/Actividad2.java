package com.dasm.carlosazanon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Actividad2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_actividad2);
	
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		String mensaje = extras.getString("dato1");
		//String mensaje = getIntent().getStringExtra("dato1");		
		Toast.makeText(Actividad2.this, mensaje, Toast.LENGTH_SHORT).show();
	}
	
	public void responderActividadUno(View v){
		Intent i=new Intent();
		i.putExtra("respuesta","OK");
		setResult(RESULT_OK,i);
		finish();
	}
	
	@Override
	public void onBackPressed(){
		Intent i=new Intent();
		i.putExtra("respuesta","Pulsado para Atras!");
		setResult(RESULT_OK,i);
		super.onBackPressed();
	}
}
