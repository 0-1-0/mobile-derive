/**
 * @author Yegorov Nickolay
 */
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import deriveUtiles.*;


public class MainMidlet extends MIDlet implements CommandListener, ItemCommandListener
{
    //const

    public static int COUNT_FUNCTIONS = 3;
    public static String PROGRAMM_HEADER = "MobileDerive v.0.0";
    public static String myOptions[] = {
		"grid",
		"axes",
		"grationg period",
		"intersection of curves",
		"coordinates of intersection of curves",
		"functions names"
    };

    //VAR
        //Display
        Display myDisplay = Display.getDisplay(this);
        //Buttons
        Command exitCommand = new Command( "exit", Command.EXIT, 1 );
        Command drawCommand = new Command( "draw graphics", Command.OK, 2 );
        Command editCommand = new Command( "edit", Command.ITEM, 2 );
        Command backCommand = new Command( "back", Command.EXIT, 3 );
	Command deriveCommand = new Command( "derivative-β", Command.ITEM, 2 );
	Command aboutCommand = new Command( "help&about", Command.HELP, 3 );
	Command clearCommand = new Command( "clear", Command.OK, 3 );
        Command optionsCommand = new Command( "options", Command.HELP, 3 );
        //Forms
        Form mainForm = new Form( PROGRAMM_HEADER );
	Form optionsForm = new Form( "options" );
	Form aboutForm = new Form( "help&Abount ");
        DrawFormCanvas myCanvas = new DrawFormCanvas();
        MyQuickEdit myEdit = new MyQuickEdit();//quick edit form
        //TextField's
        TextField textField[] = new TextField[ COUNT_FUNCTIONS ];//we makes 3 textfields for default
	//Math
        MyDeriver defaultDeriver = new MyDeriver();
	//ChoiceGroups
	ChoiceGroup isDraw     = new ChoiceGroup( "Draw", ChoiceGroup.MULTIPLE, myOptions, null );
	//StringItems
	StringItem stringItem1 = new StringItem("Zoom:\n","  zoom_in : 1\n  zoom_out: 3");
	StringItem stringItem2 = new StringItem("Move:\n","  move_UP    : 2\n  move_DOWN : 8\n  move_LEFT : 4\n  move_RIGHT: 6");
	StringItem stringItem3 = new StringItem("Default: ","0");
	StringItem stringAuthor = new StringItem("Author:\n","  Yegorov\n");
	StringItem stringVersion = new StringItem("Version\n",PROGRAMM_HEADER);
	StringItem stringUsingFunctions = new StringItem("Functions:\n"," abs pow sqrt\n exp ln\n sin cos tg ctg\n asin acos atg actg\n ");
        //Other
        //flag that shows which function is editing. -1 if nothing is editing
        private int isEditing = -1;


    public void startApp()
    {
        //make mainForm
        mainForm.addCommand(exitCommand);
        mainForm.addCommand(drawCommand);
	mainForm.addCommand(aboutCommand);
        mainForm.addCommand(optionsCommand);
        mainForm.setCommandListener(this);

        //isDraw all selected. This means that we will draw axes, coordinates & etc. by default.
        for(int i = 0; i < myOptions.length; i++)
		isDraw.setSelectedIndex( i, true );

        //make TextFields
        for(int i = 0; i < COUNT_FUNCTIONS; i++ ){
                textField[i] = new TextField( "function N" + i, "", 255, TextField.ANY  );
		textField[i].setDefaultCommand(editCommand);
                textField[i].addCommand(deriveCommand);
		textField[i].setItemCommandListener(this);
                mainForm.append( textField[i] );
        }
        myDisplay.setCurrent(mainForm); //in this block we are setting default functions in text fields
		if( COUNT_FUNCTIONS > 0 )
			textField[0].setString("sin(x)");
		if( COUNT_FUNCTIONS > 1)
			textField[1].setString("cos(x)");

	//make optionForm
	optionsForm.addCommand(backCommand);
	optionsForm.addCommand(drawCommand);
	optionsForm.setCommandListener(this);
	optionsForm.append(isDraw);
        optionsForm.append(stringItem1);
	optionsForm.append(stringItem2);
	optionsForm.append(stringItem3);

	//make aboutForm
	aboutForm.addCommand(backCommand);
	aboutForm.setCommandListener(this);
	aboutForm.append(stringAuthor);
	aboutForm.append(stringVersion);
	aboutForm.append(stringUsingFunctions);

        //make myCanvas - form for the plots of functions
        myCanvas.addCommand(backCommand);
        myCanvas.addCommand(optionsCommand);
        myCanvas.setCommandListener(this);

        //make MyQuickEdit - form for quick editing formules
        myEdit.addCommand(backCommand);
        myEdit.addCommand(clearCommand);
        myEdit.setCommandListener(this);
    }

    public void pauseApp(){}    //just Ignore

    public void destroyApp(boolean unconditional){
        notifyDestroyed();
    }

    public void commandAction(Command c, Displayable d)
    {
        if( c == exitCommand ){
            destroyApp( false );
        }
        if( c == drawCommand ){
			for(int i = 0; i < COUNT_FUNCTIONS; i++ ){
				myCanvas.setFunction( i, textField[i].getString() );
			}
			isDraw.getSelectedFlags( myCanvas.isDraw );
            myDisplay.setCurrent(myCanvas);
        }
        if( c == backCommand ){
            if( isEditing != -1 ){
                textField[ isEditing ].setString( myEdit.getString() );
            }
            myDisplay.setCurrent(mainForm);
        }
	if( c == optionsCommand ){//show options
            myDisplay.setCurrent(optionsForm);
	}
	if( c == aboutCommand ){
            myDisplay.setCurrent(aboutForm);
	}
	if( c == clearCommand ){
            if( isEditing != (-1) ) myEdit.clear();
	}
    }

    public void commandAction(Command c, Item item){
	if( c == deriveCommand ){
			for(int i = 0; i < COUNT_FUNCTIONS; i++)
				if( item == textField[i] ){
					String str = textField[i].getString();
                                        try {
                                            String[] strr = defaultDeriver.diff(str, "x");
                                            str = strr[0];
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                        textField[i].setString( str );
				}
	}
        if( c == editCommand ){
            for(int i = 0; i < COUNT_FUNCTIONS; i++)
                if( item == textField[i] )
                {
                    myDisplay.setCurrent( myEdit );
                    myEdit.setString( textField[i].getString() );
                    isEditing = i;
                }
        }

	}
}