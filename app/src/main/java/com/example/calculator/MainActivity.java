package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.mariuszgromada.math.mxparser.Expression ;


public class MainActivity extends AppCompatActivity {
    TextView txtDisplay , txtResume  , txtHistory;
    Button btnOpenParenth ;
    String resume , display ;
    boolean  digitClicked = false ,hasDot = false , equalClicked = false , openParenthActivated = false ;
    int countOpenParenth = 0 ;
    Expression expression ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtDisplay = (TextView)findViewById(R.id.txtDisplay);
        txtResume = (TextView)findViewById(R.id.txtResume);
        txtHistory = (TextView)findViewById(R.id.txtHistoryResult);
        btnOpenParenth = (Button)findViewById(R.id.btnOpenParenth) ;
    }

    public void btnDigit(View view) {

        Button btn = (Button)view ;
        String digit = btn.getText().toString();
        if(equalClicked){
            txtDisplay.setText(digit);
            txtResume.setText(digit);
            equalClicked = false ;
        }
        else if (openParenthActivated){
            txtDisplay.setText(digit);
            txtResume.setText(txtResume.getText() + digit);
            openParenthActivated = false ;
        }
        else {
            txtDisplay.setText(txtDisplay.getText() + digit);
            txtResume.setText(txtResume.getText() + digit);
        }

        digitClicked = true ;
    }

    public void btnDot(View view) {

        Button btn = (Button)view ;
        String dot = btn.getText().toString();
        resume = txtResume.getText().toString();
        display = txtDisplay.getText().toString() ;

        if( hasDot || resume.endsWith("(") || resume.endsWith(")") ){ }
        else if(equalClicked){
            clear() ;
            txtResume.setText(0 + dot);
            txtDisplay.setText(0 + dot);
            equalClicked = false ;
            hasDot = true ;
        }
        else if( resume.endsWith("+") ||
                resume.endsWith("-") ||
                resume.endsWith("*") ||
                resume.endsWith("/")  ) {
            txtResume.setText(txtResume.getText() + "0" + dot);
            txtDisplay.setText(0 + dot);
            hasDot = true ;
        }
        else {
            if(display.isEmpty()) {
                if(resume.isEmpty()) {
                    txtResume.setText(0 + dot );
                    txtDisplay.setText(0 + dot);
                } else{
                    txtResume.setText(txtResume.getText() + dot );
                    txtDisplay.setText(0 + dot);
                }
            }
            else {
                txtResume.setText(txtResume.getText() + dot);
                txtDisplay.setText(txtDisplay.getText() + dot);
            }
            hasDot = true ;
        }
    }

    public void btnOpenParenth(View view) {

        Button btn = (Button)view ;
        btn.setTransformationMethod(null);
        resume = txtResume.getText().toString();
        if( digitClicked || resume.endsWith(".") || resume.endsWith(")")) { }
        else if(equalClicked){
            txtResume.setText(null);
            txtResume.setText("(");
            openParenthActivated = true ;
            countOpenParenth++;
            btn.setText(Html.fromHtml("(<small><small>"+countOpenParenth+"</small></small>")) ;
            equalClicked = false ;
            digitClicked = false ;
        }
        else {
            txtResume.setText(txtResume.getText() + "(");
            countOpenParenth++;
            btn.setText(Html.fromHtml("(<small><small>"+countOpenParenth+"</small></small>")) ;
            hasDot = false ;
            digitClicked = false ;
        }
    }

    public void btnCloseParenth(View view) {

        resume = txtResume.getText().toString();
        if(equalClicked || countOpenParenth == 0){

        }
        else if( resume.isEmpty() || resume.endsWith("(")  ||
                resume.endsWith("+") || resume.endsWith("-") ||
                resume.endsWith("*") || resume.endsWith("/") ||
                resume.endsWith(".")){}
        else {
            txtResume.setText( resume + ")" );
            countOpenParenth--;
            if(countOpenParenth == 0){ btnOpenParenth.setText( "(" ) ; }
            else btnOpenParenth.setText(Html.fromHtml("(<small><small>"+countOpenParenth+"</small></small>")) ;
            digitClicked = false ;
            hasDot = false ;
            equalClicked = false ;
        }
    }

    public void btnOperation(View view) {

        Button btn = (Button)view ;
        String operation = btn.getText().toString();
        resume = txtResume.getText().toString();
        display = txtDisplay.getText().toString();
        if(equalClicked){
            txtResume.setText( display + operation);
            txtDisplay.setText(null);
            equalClicked = false ;
            digitClicked = false ;
            hasDot = false ;
        }
        else if (openParenthActivated){
            txtResume.setText(txtResume.getText() + display+ operation );
            openParenthActivated = false ;
            txtDisplay.setText(null);
            digitClicked = false ;
            hasDot = false ;
        }
        else if ( operation.equals("-")){
            if( resume.endsWith("+") || resume.endsWith("-")){

            }
            else {
                txtResume.setText(resume + operation);
                txtDisplay.setText(null);
                digitClicked = false ;
                hasDot = false ;
            }
        }
        else if( resume.isEmpty() || resume.endsWith("(")  ||
                resume.endsWith("+") || resume.endsWith("-") ||
                resume.endsWith("*") || resume.endsWith("/") ||
                resume.endsWith(".")){}
        else {
            txtResume.setText(resume + operation);
            txtDisplay.setText(null);
            digitClicked = false ;
            hasDot = false ;
        }
    }

    public void btnEqual(View view) {

        resume = txtResume.getText().toString();
        if( equalClicked || resume.isEmpty() || countOpenParenth > 0 || resume.endsWith("(") || resume.endsWith("+") || resume.endsWith("-") ||
                resume.endsWith("*") || resume.endsWith("/") || resume.endsWith(".")){}
        else {
            expression = new Expression(resume) ;
            Double result = expression.calculate() ;
            if(result.isNaN()){
                txtHistory.setText( resume +"        "+" Error : can't divide by 0 !");
                clear() ;
            }
            else {
                txtDisplay.setText(String.valueOf(result));
                txtResume.setText(resume + " = " );
                txtHistory.setText(resume + " = " + result);
                digitClicked = false ;
                equalClicked = true ;
            }
        }
    }

    public void btnClear(View view) {
        clear() ;
    }

    public void btnDelete(View view) {

        resume = txtResume.getText().toString();
        display = txtDisplay.getText().toString();
        if(resume.isEmpty() || equalClicked ){
            clear() ;
        }
        else {
            if(resume.endsWith(".")){
                removeLastElement();
                hasDot = false ;
            }
            else if (resume.endsWith("(")){
                removeLastElement();
                countOpenParenth -- ;
                if(countOpenParenth == 0){
                    btnOpenParenth.setText( "(" ) ;
                }
                else btnOpenParenth.setText(Html.fromHtml("(<small><small>"+countOpenParenth+"</small></small>")) ;
            }
            else if (resume.endsWith(")")){
                removeLastElement();
                countOpenParenth++;
                btnOpenParenth.setText(Html.fromHtml("(<small><small>"+countOpenParenth+"</small></small>")) ;
                String newResume = txtResume.getText().toString();
                String lastDigit =lastDigit(newResume);
                if(lastDigit.contains("."))
                {
                    txtDisplay.setText(lastDigit);
                    hasDot = true ;
                }
                else {
                    txtDisplay.setText(lastDigit);
                    hasDot = false ;
                }
                digitClicked = true ;
            }
            else if(resume.endsWith("+") || resume.endsWith("-") ||
                    resume.endsWith("*") || resume.endsWith("/")){
                removeLastElement();
                String newResume = txtResume.getText().toString();
                String lastDigit =lastDigit(newResume);
                if(lastDigit.contains("."))
                {
                    txtDisplay.setText(lastDigit);
                    hasDot = true ;
                }
                else {
                    txtDisplay.setText(lastDigit);
                    hasDot = false ;
                }
                digitClicked = true ;
            }
            else {
                removeLastElement();
                digitClicked = false ;
            }
            equalClicked = false ;
        }

    }

    public void clear() {
        txtDisplay.setText(null);
        txtResume.setText(null);
        btnOpenParenth.setText("(");
        display = "";
        resume = "" ;
        countOpenParenth = 0 ;
        digitClicked = false ;
        hasDot = false ;
        equalClicked = false ;
        openParenthActivated = false ;
    }

    public void removeLastElement() {
        if(!display.isEmpty()){
            txtDisplay.setText(display.substring(0, txtDisplay.getText().length() - 1));
        }
        txtResume.setText(resume.substring(0, txtResume.getText().length() - 1));
    }

    public String lastDigit(String s) {
        String lastDigit = "" ;
        for( int i = s.length() - 1  ; i >= 0 ; i--)
        {
            char c = s.charAt(i) ;
            if(c != '+' && c != '-' && c != '*' && c != '/' &&
                    c != '(' && c != ')'  )
            {
                lastDigit = lastDigit + c ;
            }
            else break ;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(lastDigit);
        sb = sb.reverse();
        return sb.toString() ;

    }
}
