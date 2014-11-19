/*
 * 계산기 UI 선언부
 *  
 */

package com.Project;

import java.util.List;

import com.Project.Calculation;
import com.example.calculator.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CalActivity extends Activity implements OnClickListener {


	// 식을 표현하기 위한 Edit Box 만들기
	private EditText Operand_1;
	private EditText Operand_2;
	private EditText Result;
	private String Operator;
	private int _operand_1;
	private int _operand_2;
	private double result;

	// 버튼을 위한 배열 만들기
	private Button btn[];

	// 계산을 위해서 Calculation Object 생성.
	Calculation cal = new Calculation();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// XML에서 선언한 Item 등록
		Operand_1 = (EditText) findViewById(R.id.Operand_1);
		Operand_2 = (EditText) findViewById(R.id.Operand_2);
		Result = (EditText) findViewById(R.id.Result);

		btn = new Button[5];
		btn[0] = (Button) findViewById(R.id.btn_1);
		btn[1] = (Button) findViewById(R.id.btn_2);
		btn[2] = (Button) findViewById(R.id.btn_3);
		btn[3] = (Button) findViewById(R.id.btn_4);
		btn[4] = (Button) findViewById(R.id.btn_5);

		for (int i = 0; i < btn.length; i++) {
			// btn 배열에 SetOnClickListener로 등록.
			btn[i].setOnClickListener(this);
		}

	}
	public void onClick(View v) {
		// 각 버튼과 Method 맵핑.
		switch (v.getId()) {
		case R.id.btn_1:
			Operator = "+";
			break;
		case R.id.btn_2:
			Operator = "-";
			break;
		case R.id.btn_3:
			Operator = "*";
			break;
		case R.id.btn_4:
			Operator = "/";
			break;
		case R.id.btn_5:
		
			String _tmp_Operand_1 = Operand_1.getText().toString();
			String _tmp_Operand_2 = Operand_2.getText().toString();
			
			if((_tmp_Operand_1 != null) &&(_tmp_Operand_2 != null)&&(Operator != null ))
			{	
				_operand_1 = Integer.valueOf(_tmp_Operand_1);
				_operand_2 = Integer.valueOf(_tmp_Operand_2);
				
				result = cal.getResult(_operand_1, _operand_2, Operator);
				Result.setText(String.valueOf(result));
			}
			else 
			{
				Toast.makeText(CalActivity.this, "Error", Toast.LENGTH_SHORT).show();
			}
				
				
			
			break;
		}
	}

}
