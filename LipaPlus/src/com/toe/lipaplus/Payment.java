package com.toe.lipaplus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cardreader.audio.app.SComboReader_DUKPT;
import com.cardreader.audio.app.SComboReader_DUKPT.ArrayByteValue;
import com.cardreader.audio.sdk.AudioSeries;
import com.cardreader.audio.sdk.AudioSeries.AudioSeriesPort;
import com.cardreader.audio.sdk.FileService;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42CacheManager.Policy;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

public class Payment extends SherlockFragmentActivity {

	RecieptDataCustomDialog rdcDialog;
	SherlockFragmentActivity activity;
	ArrayList<String> productNames = new ArrayList<String>();
	ArrayList<String> productDescriptions = new ArrayList<String>();
	ArrayList<String> productPrices = new ArrayList<String>();
	float totalCost;
	TextView tvTotalCost, tvTitle;
	Button bMobileMoney, bCash;
	int day, month, year, dayOfYear, weekOfYear, monthOfYear,
			numberOfItems = 0;
	String transactionType;
	SharedPreferences sp;
	StorageService storageService;
	SComboReader_DUKPT sComboReader;
	private static final String TAG = "Payment";
	// Detect Phone Jack plugged
	private boolean headsetConnected = false;
	public static final int MSG_STARTINIT = 0x01;
	public static final int MSG_INITDONE = 0x02;
	public static final int MSG_INITFAULT = 0x03;
	public static final int MSG_REINIT = 0x04;

	public String str_Encrypt;
	public String str_Decrypt_Key;

	public final String str_Four = "04";
	public final String str_Six = "06";
	public final String str_Eight = "08";
	public final String str_Twelve = "12";

	public String str_Encrypt_Method;
	public String str_KSN = null;
	public String str_Encrypt_DUKPT = null;

	public final String str_No = "00";
	public final String str_Des = "10";
	public final String str_3DES_16 = "20";
	public final String str_3DES_24 = "21";
	public final String str_AES_16 = "30";
	public final String str_AES_24 = "31";
	public final String str_AES_32 = "32";
	public final String str_DUKPT = "40";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initialize();
		getBundle();
		setUp();
		// initializeCardReader();
		demo();
	}

	private void demo() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final ProgressDialog progress = ProgressDialog.show(
						Payment.this, "Please wait", "Listening for device...",
						true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// do the thing that takes a long time
						try {
							Thread.sleep(5000);
							progress.dismiss();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
		}, 2000);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				final ProgressDialog progress = ProgressDialog.show(
						Payment.this, "Please wait", "Processing payment...",
						true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// do the thing that takes a long time
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								SmsManager smsManager = SmsManager.getDefault();
								smsManager
										.sendTextMessage(
												"+254720026127",
												null,
												"Txn E76fcx76 successfull. You have transacted Ksh."
														+ totalCost
														+ " using at Masha Groceries Ngong Road. Have a nice day.\n\n-Powered by LipaPlus",
												null, null);
								Toast.makeText(getApplicationContext(),
										"Transaction was successful",
										Toast.LENGTH_SHORT).show();
								progress.dismiss();
							}
						});
					}
				}).start();
			}
		}, 14000);
	}

	private void initialize() {
		// TODO Auto-generated method stub
		App42API.initialize(getApplicationContext(),
				getString(R.string.api_key), getString(R.string.secret_key));
		App42CacheManager.setPolicy(Policy.NETWORK_FIRST);
		storageService = App42API.buildStorageService();
	}

	private void getBundle() {
		// TODO Auto-generated method stub
		Bundle b = getIntent().getExtras();
		productNames = b.getStringArrayList("productNames");
		productDescriptions = b.getStringArrayList("productDescriptions");
		productPrices = b.getStringArrayList("productPrices");

		for (int i = 0; i < productPrices.size(); i++) {
			totalCost += Float.parseFloat(productPrices.get(i));
		}
	}

	private void setUp() {
		// TODO Auto-generated method stub
		activity = this;
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		Typeface font = Typeface.createFromAsset(getAssets(), getResources()
				.getString(R.string.font));

		tvTotalCost = (TextView) findViewById(R.id.tvTotalCost);
		tvTotalCost.setTypeface(font);
		tvTotalCost.setText("Ksh. " + totalCost);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);
		tvTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rdcDialog = new RecieptDataCustomDialog(activity);
				rdcDialog.getWindow().setBackgroundDrawable(
						new ColorDrawable(Color.TRANSPARENT));
				rdcDialog.title = "Send the reciept";
				rdcDialog.show();
				rdcDialog.bDone.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method
						// stub
						String emailAddress = rdcDialog.etEmailAddress
								.getText().toString().trim();
						String phoneNumber = rdcDialog.etPhoneNumber.getText()
								.toString().trim();

						if (emailAddress.length() > 5) {
							Intent emailIntent = new Intent(
									Intent.ACTION_SENDTO, Uri.fromParts(
											"mailto", emailAddress, null));
							emailIntent.putExtra(Intent.EXTRA_SUBJECT,
									"Powered by Lipa Plus");
							startActivity(Intent.createChooser(emailIntent,
									"Send email..."));
						}

						if (phoneNumber.length() > 5) {
							Intent smsIntent = new Intent(Intent.ACTION_VIEW);
							smsIntent.setType("vnd.android-dir/mms-sms");
							smsIntent.putExtra("address", phoneNumber);
							smsIntent.putExtra("sms_body",
									"Powered by Lipa Plus:");
							startActivity(smsIntent);
						}

						rdcDialog.dismiss();
					}
				});
			}
		});

		bMobileMoney = (Button) findViewById(R.id.bMobileMoney);
		bMobileMoney.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				day = Calendar.DAY_OF_MONTH;
				month = Calendar.MONTH;
				year = Calendar.YEAR;
				dayOfYear = Calendar.DAY_OF_YEAR;
				weekOfYear = Calendar.WEEK_OF_YEAR;
				transactionType = "Mobile Money";
				numberOfItems = productNames.size();

				saveTransaction(new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(Calendar.getInstance().getTime()), year,
						dayOfYear, weekOfYear, transactionType, numberOfItems,
						totalCost);
			}
		});

		bCash = (Button) findViewById(R.id.bCash);
		bCash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dayOfYear = Calendar.DAY_OF_YEAR;
				weekOfYear = Calendar.WEEK_OF_YEAR;
				transactionType = "Cash";
				numberOfItems = productNames.size();

				saveTransaction(new SimpleDateFormat("yyyymmdd_HHmmss")
						.format(Calendar.getInstance().getTime()), year,
						dayOfYear, weekOfYear, transactionType, numberOfItems,
						totalCost);
			}
		});
	}

	protected void saveTransaction(String date, int year, int dayOfYear,
			int weekOfYear, String transactionType, int numberOfItems,
			float totalCost) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		try {
			json.put("date", date);
			json.put("year", year);
			json.put("dayOfYear", dayOfYear);
			json.put("weekOfYear", weekOfYear);
			json.put("transactionType", transactionType);
			json.put("numberOfItems", numberOfItems);
			json.put("totalCost", totalCost);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		storageService.insertJSONDocument(getString(R.string.database_name),
				sp.getString("userEmail", null) + "_transactions", json,
				new App42CallBack() {
					public void onSuccess(Object response) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(),
										"Transaction saved successfully!",
										Toast.LENGTH_SHORT).show();

								rdcDialog = new RecieptDataCustomDialog(
										activity);
								rdcDialog.getWindow().setBackgroundDrawable(
										new ColorDrawable(Color.TRANSPARENT));
								rdcDialog.title = "Send the reciept";
								rdcDialog.show();
								rdcDialog.bDone
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												// TODO Auto-generated method
												// stub
												String emailAddress = rdcDialog.etEmailAddress
														.getText().toString()
														.trim();
												String phoneNumber = rdcDialog.etPhoneNumber
														.getText().toString()
														.trim();

												if (emailAddress.length() > 5) {
													Intent emailIntent = new Intent(
															Intent.ACTION_SENDTO,
															Uri.fromParts(
																	"mailto",
																	emailAddress,
																	null));
													emailIntent
															.putExtra(
																	Intent.EXTRA_SUBJECT,
																	"Powered by Lipa Plus");
													startActivity(Intent
															.createChooser(
																	emailIntent,
																	"Send email..."));
												}

												if (phoneNumber.length() > 5) {
													Intent smsIntent = new Intent(
															Intent.ACTION_VIEW);
													smsIntent
															.setType("vnd.android-dir/mms-sms");
													smsIntent.putExtra(
															"address",
															phoneNumber);
													smsIntent
															.putExtra(
																	"sms_body",
																	"Powered by Lipa Plus:");
													startActivity(smsIntent);
												}

												rdcDialog.dismiss();
											}
										});
							}
						});
					}

					public void onException(Exception ex) {
						System.out.println("Exception Message"
								+ ex.getMessage());
					}
				});
	}

	private void initializeCardReader() {
		// TODO Auto-generated method stub
		ShowMessage(activity, "Message", "Please insert SS506-P16 Reader.",
				onDlgClick);
		registerReceiver(mHeadsetReceiver, new IntentFilter(
				Intent.ACTION_HEADSET_PLUG));
	}

	private BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
				boolean hasHeadset = (intent.getIntExtra("state", 0) == 1);
				boolean hasMicroPhone = (intent.getIntExtra("microphone", 0) == 1);
				if (hasHeadset && hasMicroPhone) {
					headsetConnected = true;
					CreateAudio();
				} else {
					headsetConnected = false;
				}
			}
		}
	};

	private void CreateAudio() {
		// Alloc a Singular Reader object
		if (sComboReader == null) {
			// Alloc a AudioSeriesPort parameter
			AudioSeriesPort audioseriesport = new AudioSeriesPort();
			sComboReader = new SComboReader_DUKPT();
			sComboReader.RegisterMessage(myMessageHandler);
			// Open SComboReader Audio Device
			if (sComboReader.SComboReaderOpen(audioseriesport) != SComboReader_DUKPT.OK) {
				ShowMessage(this, "System Error", "Can't open audio device.");
				return;
			}
		}
	}

	public Handler myMessageHandler = new Handler() {

		public void handleMessage(Message msg) {

			int SW = msg.arg1;
			switch (msg.what) {
			case SComboReader_DUKPT.CMD_Get_Version: {
				ArrayByteValue val = (ArrayByteValue) msg.obj;

				if (SW == SComboReader_DUKPT.OK) {
					DispVersion(val);
				} else {
					DispState(SW);
				}
				break;
			}

			case SComboReader_DUKPT.CMD_Detect_Battery_Energy: {
				ArrayByteValue val = (ArrayByteValue) msg.obj;

				if (SW == SComboReader_DUKPT.OK) {
					DispEnergy(val);
				} else {
					DispState(SW);
				}
				break;
			}
			case SComboReader_DUKPT.CMD_Reset_Chip:
			case SComboReader_DUKPT.CMD_Set_Time_Into_Power_Down:
			case SComboReader_DUKPT.CMD_Select_Encrypt_Mode: {
				DispState(SW);
				break;
			}
			}

			super.handleMessage(msg);
		}
	};

	// Display Status
	private void DispState(int SW) {
		this.PutMessage(String.format("State: %s.", sComboReader.GetCSW(SW)));
	}

	// Display Receive version
	private void DispVersion(ArrayByteValue versionString) {

		java.lang.StringBuilder sb = new java.lang.StringBuilder();

		for (int i = 0; i < versionString.Value.length; i++) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(String.format("%s", (char) versionString.Value[i]));
		}

		this.PutMessage(sb.toString());
	}

	// Display Energy
	private void DispEnergy(ArrayByteValue energyString) {

		java.lang.StringBuilder sb = new java.lang.StringBuilder();

		for (int i = 0; i < energyString.Value.length; i++) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(String.format("%02x", energyString.Value[i]));
		}

		if (sb.toString().equalsIgnoreCase("00")) {
			this.PutMessage("Battery empty");
		} else if (sb.toString().equalsIgnoreCase("01")) {
			this.PutMessage("Battery 1/3");
		} else if (sb.toString().equalsIgnoreCase("02")) {
			this.PutMessage("Battery 2/3");
		} else if (sb.toString().equalsIgnoreCase("03")) {
			this.PutMessage("Battery full");
		}
	}

	private void PutMessage(String string) {
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT)
				.show();
	}

	public static void ShowMessage(Context context, String title, String msg) {
		ShowMessage(context, title, msg, null);
	}

	public static void ShowMessage(Context context, String title, String msg,
			final View.OnClickListener onClickListener) {
		String btn = "OK";

		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setTitle(title);
		dlg.setMessage(msg);
		dlg.setPositiveButton(btn, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				if (onClickListener != null) {
					onClickListener.onClick(null);
				}
			}
		});

		dlg.show();
	}

	private ProgressDialog myDialog;
	private View.OnClickListener onDlgClick = new View.OnClickListener() {

		public void onClick(View v) {

			if (!headsetConnected) {
				ShowMessage(activity, "Message",
						"Please insert SS506-P16 Reader.", onDlgClick);
				return;
			}
			// Start SS506-P16 Reader processing
			StartInit();
		}
	};

	private void StartInit() {

		// Volume to the maximum
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC,
				am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

		// Start SS506-P16 Reader processing

		if (sComboReader.SComboReaderStart() != SComboReader_DUKPT.OK) {
			ShowMessage(activity, "Message", "SS506-P16 Reader Open Fail.");
			return;
		}

		// First time, must send Initialization data to Singular Reader
		myDialog = ProgressDialog.show(activity, "Message",
				"System Initialization...");
		new InitThread().start();

	}

	private void ReInit() {

		myDialog = ProgressDialog.show(activity, "Message",
				"System Initialization...");
		new ReInitThread().start();
	}

	private class InitThread extends Thread {
		@Override
		public void run() {
			if (sComboReader.SComboReaderInit() == SComboReader_DUKPT.OK) {
				SendMessage(MSG_INITDONE, 0, 0);
			} else
				SendMessage(MSG_REINIT, 0, 0);
		}
	};

	private class ReInitThread extends Thread {
		public void run() {

			if (sComboReader.SComboReaderReInit() == SComboReader_DUKPT.OK) {
				SendMessage(MSG_INITDONE, 0, 0);

				FileService save = new FileService(activity);
				try {
					save.save();
				} catch (Exception e) {
					// TODO 自動產生的 catch 區塊
					e.printStackTrace();
				}
			}

			else
				SendMessage(MSG_INITFAULT, 0, 0, "Initial Fail.");
		}
	};

	private void SendMessage(int i_msg, int arg1, int arg2) {
		Message msg = new Message();
		msg.what = i_msg;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		myHandler.sendMessage(msg);
	}

	private void SendMessage(int i_msg, int arg1, int arg2, Object obj) {
		Message msg = new Message();
		msg.what = i_msg;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		msg.obj = obj;
		myHandler.sendMessage(msg);
	}

	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {

			Toast.makeText(
					getApplicationContext(),
					msg.what + " " + msg.arg1 + " "
							+ SComboReader_DUKPT.CMD_ICC_Information,
					Toast.LENGTH_SHORT).show();

			int SW = msg.arg1;
			switch (msg.what) {
			case MSG_STARTINIT: {

				if (!headsetConnected) {
					ShowMessage(activity, "Message",
							"Please insert SS506-P16 Reader.", onDlgClick);
				} else {
					StartInit();
				}
				break;
			}
			case MSG_INITFAULT: {
				myDialog.dismiss();

				if (msg.obj != null) {
					String str_msg = (String) msg.obj;
					ShowMessage(activity, "Message", str_msg, null);
				}
				break;
			}
			case MSG_INITDONE: {
				myDialog.dismiss();
				PutMessage("Initial Success.");
				demo();
				cardReaderFunction();
				break;
			}
			case MSG_REINIT: {

				myDialog.dismiss();
				if (!headsetConnected) {
					ShowMessage(activity, "Message",
							"Please insert SS506-P16 Reader.", onDlgClick);
				} else {
					ReInit();
				}
				break;
			}
			case SComboReader_DUKPT.CMD_ICC_Information: {
				ArrayByteValue val = (ArrayByteValue) msg.obj;
				if (SW == SComboReader_DUKPT.OK) {
					DispValueInformation(val);
				} else {
					DispState(SW);
				}
				break;
			}
			case SComboReader_DUKPT.CMD_Get_PIN: {
				ArrayByteValue val = (ArrayByteValue) msg.obj;

				if (AudioSeries.giveupAction == true) {
					SystemClock.sleep(20);
					Send_GiveupAction();
				} else {
					if (SW == SComboReader_DUKPT.OK) {
						try {
							DispPINValue(val);
						} catch (Exception e) {

							e.printStackTrace();
						}
					} else {
						DispState(SW);
					}
				}
				break;
			}
			case SComboReader_DUKPT.CMD_Give_Up_Action: {
				DispState(SW);
				break;
			}
			}

			super.handleMessage(msg);
		}
	};

	private void cardReaderFunction() {
		// TODO Auto-generated method stub
		PutMessage(">>>>>>CardInformation()");
		sComboReader.ICCInformation(null);
	}

	private void DispValueInformation(ArrayByteValue insertString) {

		java.lang.StringBuilder sb = new java.lang.StringBuilder();

		for (int i = 0; i < insertString.Value.length; i++) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(String.format("%02x", insertString.Value[i]));
		}

		String card_data = sb.toString().replace(" ", "");
		;
		;

		int card_num = card_data.lastIndexOf("a1");
		int card_name = card_data.lastIndexOf("b1");
		int card_exp = card_data.lastIndexOf("c1");

		String card_number = changeHexString2CharString(card_data.substring(
				card_num + 2, card_name));
		String user_name = changeHexString2CharString(card_data.substring(
				card_name + 2, card_exp));
		String expired_date = changeHexString2CharString(card_data.substring(
				card_exp + 2, card_data.length()));
		expired_date = expired_date.substring(0, 2) + "/"
				+ expired_date.substring(2, expired_date.length());

		int card_type1 = Integer.parseInt(card_number.substring(0, 1));
		int card_type2 = Integer.parseInt(card_number.substring(0, 3)); // 3
		int card_type3 = Integer.parseInt(card_number.substring(0, 4));
		String type;
		if (card_type1 == 4) {
			type = "Visa Card";
		} else if (card_type1 == 5) {
			type = "Master Card";
		} else if ((card_type1 == 3)
				&& (card_type2 >= 340 && card_type2 <= 379)
				&& (card_number.length() == 15)) {
			type = "AE Card";
		} else if (((card_type1 == 1) && (card_type3 == 1800))
				|| ((card_type1 == 2) && (card_type3 == 2131)))//
		{
			type = "JCB Card";

		} else if ((card_type2 >= 300 && card_type2 <= 399)
				&& (card_number.length() == 16) || ((card_type1 == 3))) {
			type = "JCB Card";
		} else {
			type = "Card";
		}

		PutMessage("Card Type : " + type);
		PutMessage("Card Number :  " + card_number);
		PutMessage("Expired Date :  " + expired_date);
		PutMessage("Name : " + user_name);

	}

	public static String changeHexString2CharString(String e) {
		String char_txt = "";
		for (int i = 0; i < e.length(); i = i + 2) {
			String c = e.substring(i, i + 2);
			char j = (char) Integer.parseInt(c, 16);
			char_txt += j;
		}
		return char_txt;
	}

	private void SendSetPinpadMode() {
		PutMessage(">>>>>>SetPinpadMode()");
		String str_Time = "30";
		String str_Len = "4";

		int time = 0;
		int len = 0;

		try {
			time = Integer.valueOf(str_Time);
			len = Integer.valueOf(str_Len);
		} catch (Exception e) {
			// TODO 自動產生的 catch 區塊
			e.printStackTrace();
		}

		sComboReader.SetPinpadMode(len, time);
	}

	private void SendGetPIN() {
		Toast.makeText(getApplicationContext(), "Please enter the PIN",
				Toast.LENGTH_SHORT).show();
		PutMessage(">>>>>>GetPIN()");
		sComboReader.GetPIN(null);
	}

	// // Display Receive data

	private void DispPINValue(ArrayByteValue PINString) {

		java.lang.StringBuilder sb = new java.lang.StringBuilder();

		for (int i = 0; i < PINString.Value.length; i++) {
			sb.append(String.format("%02X", PINString.Value[i]));
		}

		str_Encrypt = sb.toString();

		String str_Display_Encrypt = sComboReader
				.changeHexString2CharStringData(sb.toString());
		str_Decrypt_Key = str_No;
		if (str_Encrypt_Method.equals(str_No)) {
			this.PutMessage("No Encrypt :" + str_Display_Encrypt);
		} else if (str_Encrypt_Method.equals(str_Des)) {
			String str_Des_Decrypt = sComboReader.DesDecryption(
					str_Decrypt_Key, str_Encrypt);
			this.PutMessage("DES \n"
					+ "Encrypt :"
					+ str_Encrypt
					+ "\n"
					+ "Decrypt :"
					+ sComboReader
							.changeHexString2CharStringData(str_Des_Decrypt));
		} else if (str_Encrypt_Method.equals(str_3DES_16)) {
			String str_TriDes_Decrypt_16 = sComboReader.TriDesDecryption(
					str_Decrypt_Key, str_Encrypt);
			this.PutMessage("3DES 16 byte\n"
					+ "Encrypt :"
					+ str_Encrypt
					+ "\n"
					+ "Decrypt :"
					+ sComboReader
							.changeHexString2CharStringData(str_TriDes_Decrypt_16));

		} else if (str_Encrypt_Method.equals(str_3DES_24)) {
			String str_TriDes_Decrypt_24 = sComboReader.TriDesDecryption(
					str_Decrypt_Key, str_Encrypt);
			this.PutMessage("3DES 24 byte\n"
					+ "Encrypt :"
					+ str_Encrypt
					+ "\n"
					+ "Decrypt :"
					+ sComboReader
							.changeHexString2CharStringData(str_TriDes_Decrypt_24));
		} else if (str_Encrypt_Method.equals(str_AES_16)) {
			String str_AES_Decrypt_16 = sComboReader.AESDecryption(
					str_Decrypt_Key, str_Encrypt);
			this.PutMessage("AES 16 byte\n"
					+ "Encrypt: "
					+ str_Encrypt
					+ "\n"
					+ "Decrypt :"
					+ sComboReader
							.changeHexString2CharStringData(str_AES_Decrypt_16));
		} else if (str_Encrypt_Method.equals(str_AES_24)) {
			String str_AES_Decrypt_24 = sComboReader.AESDecryption(
					str_Decrypt_Key, str_Encrypt);
			this.PutMessage("AES 24 byte\n"
					+ "Encrypt :"
					+ str_Encrypt
					+ "\n"
					+ "Decrypt :"
					+ sComboReader
							.changeHexString2CharStringData(str_AES_Decrypt_24));
		} else if (str_Encrypt_Method.equals(str_AES_32)) {
			String str_AES_Decrypt_32 = sComboReader.AESDecryption(
					str_Decrypt_Key, str_Encrypt);
			this.PutMessage("AES 32 byte\n"
					+ "Encrypt :"
					+ str_Encrypt
					+ "\n"
					+ "Decrypt :"
					+ sComboReader
							.changeHexString2CharStringData(str_AES_Decrypt_32));
		} else if (str_Encrypt_Method.equals(str_DUKPT)) {

			str_KSN = str_Encrypt.substring(0, 16);
			Log.v(TAG, "KSN=" + str_KSN);
			str_Encrypt_DUKPT = str_Encrypt.substring(16);
			Log.v(TAG, "encryption=" + str_Encrypt_DUKPT);

			if (str_Encrypt_DUKPT == null || str_Encrypt_DUKPT.length() == 0)
				return;

			String bdk = str_Decrypt_Key;
			Log.v(TAG, "BDK=" + bdk);
			String LMKSN = "FFFF" + str_KSN.substring(0, 12);
			Log.v(TAG, "LMKSN=" + LMKSN);
			String ipek = sComboReader.GenerateIPEK(bdk, LMKSN);
			Log.v(TAG, "IPEK=" + ipek);
			String ctkey = sComboReader.GenerateCTKey(ipek, str_KSN);
			Log.v(TAG, "CTKEY=" + ctkey);

			// Decrypto
			String str_DUKPT_Decrypt = sComboReader.TriDesDecryption(ctkey,
					str_Encrypt_DUKPT);
			this.PutMessage("DUKPT\n"
					+ "Encrypt :"
					+ str_Encrypt_DUKPT
					+ "\n"
					+ "Decrypt :"
					+ sComboReader
							.changeHexString2CharStringData(str_DUKPT_Decrypt));
		}

	}

	private void Send_GiveupAction() {
		PutMessage(">>>>>>PINPadGiveupAction()");
		sComboReader.GiveupAction();

		AudioSeries.giveupAction = false;
	}

	// public void onDestroy() {
	// unregisterReceiver(mHeadsetReceiver);
	// super.onDestroy();
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.payments_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.mInputPin:
			// SendSetPinpadMode();
			sComboReader.ICCPowerOn(null);
			SendGetPIN();
			break;
		case R.id.mInitialize:
			ShowMessage(activity, "Message", "Please insert SS506-P16 Reader.",
					onDlgClick);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
}
