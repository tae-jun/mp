/*
 *  ���� ����� �ϴ� Ŭ���� 
 *  
 */

package com.Project;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Calculation {

	public double getResult(int _operand_1, int _operand_2, String Operator) 
	{
		char 	_operator = Operator.charAt(0);
		double 	result = 0;
		switch(_operator)
		{
			case '+':
				result = sum(_operand_1, _operand_2);
				break;
			case '-':
				result = sub(_operand_1, _operand_2);
				break;
			case '*':
				result = mul(_operand_1, _operand_2);
				break;
			case '/':
				result = div(_operand_1, _operand_2);
				break;
		}
				
		return result;
	}
	// - ����
	private Double sub(int i, int j) {
		return (double) (i - j);
	}
	// / ����
	private Double div(int i, int j) {
		if (j != 0)
		{
			return (double) (i / j);
		}
		
		return 0.0;
	}
	// * ����
	private Double mul(int i, int j) {
		return (double) (i) * (double)(j);
	}
	// + ����
	private Double sum(int i, int j) {
		return (double) (i + j);
	}

}
