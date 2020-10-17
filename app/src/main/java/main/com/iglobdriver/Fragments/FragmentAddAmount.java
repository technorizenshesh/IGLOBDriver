package main.com.iglobdriver.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import main.com.iglobdriver.R;
import main.com.iglobdriver.activity.CashOutAct;
import main.com.iglobdriver.activity.WalletAct;
import main.com.iglobdriver.databinding.FragmentAddAmountBinding;
import main.com.iglobdriver.databinding.FragmentAddPayoutBinding;
import main.com.iglobdriver.paymentclasses.MyCardsPayment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddAmount extends Fragment {
    private FragmentAddAmountBinding binding;
    private String amount_str="0";

    public FragmentAddAmount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_add_amount, container, false);
        bindView();
        return binding.getRoot();
    }

    private void bindView() {
        binding.withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CashOutAct.class);
                startActivity(i);
            }
        });
        binding.addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_str = binding.amountEt.getText().toString();
                if (amount_str.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),getResources().getString(R.string.enteramount),Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(getActivity(), MyCardsPayment.class);
                    i.putExtra("amount_str",amount_str);
                    startActivity(i);

                }
            }
        });
        binding.fiftyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.amountEt.setText("50");
                binding.fiftyBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                binding.hundredBut.setBackgroundResource(R.drawable.border_grey_rec);
                binding.onefiftyBut.setBackgroundResource(R.drawable.border_grey_rec);

            }
        });
        binding.hundredBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.amountEt.setText("100");
                binding.hundredBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                binding.onefiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
                binding.fiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });

        binding.onefiftyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.amountEt.setText("150");
                binding.onefiftyBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                binding.hundredBut.setBackgroundResource(R.drawable.border_grey_rec);
                binding.fiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });

    }
}
