import Objects.*;
import View.*;
import java.time.LocalDate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller implements ActionListener {

    private Model model;
    private View view;

    public Controller(Model model, View view) throws SQLException {
        this.model = model;
        this.view = view;
        addActionListenerToButtons();
        model.addPaymentInformation("1234","123","1928","06","2",model.getUserArrayList().get(0));
    }

    public void addActionListenerToButtons(){
        for(int i = 0; i<view.getButtons().size(); i++){
            view.getButtons().get(i).addActionListener(this);
        }
    }

    /**
     * burda kullanıcı ne yapmış nelere basmış kontrol edip ona göre modelin fonksiyonlarını kullanıcaz
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        SignUpView signUpView = view.getsView();
        LoginView lv = view.getlView();
        UserMainView userMainView = view.getuView();

        if(e.getSource()==signUpView.getSignUpButton()){

            String username = signUpView.getUserNameTextField().getText();
            String password = signUpView.getPasswordTextField().getText();
            String passwordCheck = signUpView.getPasswordCheckTextField().getText();
            String nameSurname = signUpView.getUserNameTextField().getText();
            String eMail = signUpView.getMailTextField().getText();
            String phoneNumber = signUpView.getPhoneNumberTextField().getText();
            String age = signUpView.getBirthdayTextField().getText();
            String gender=null;
            if(signUpView.getFemaleButton().isSelected()){
                gender="female";
            }else if(signUpView.getMaleButton().isSelected()){
                gender="male";
            }

            try {
                if(model.signUp(username, password, passwordCheck, eMail, age, phoneNumber, gender)==true){
                    signUpView.setVisible(false);
                    lv.getPasswordTextField().setText("");
                    lv.getUserNameTextField().setText("");
                    lv.setVisible(true);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }


        }

        if(e.getSource()==lv.getLogInButton()){
            String username=lv.getUserNameTextField().getText();
            String password=lv.getPasswordTextField().getText();
            try {
                if(model.logIn(username,password)==true){
                    lv.setVisible(false);
                    ArrayList<String> tempList = getUsersReservations(username);
                    String[][] tempArray = getData(tempList);
                    userMainView.setData(tempArray);
                    userMainView.createTable();
                    userMainView.setVisible(true);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            lv.getPasswordTextField().setText("");
            lv.getUserNameTextField().setText("");
        }
        if(e.getSource()==lv.getSignUpButton()){
            lv.setVisible(false);
            signUpView.getUserNameTextField().setText("");
            signUpView.getPasswordTextField().setText("");
            signUpView.getPasswordCheckTextField().setText("");
            signUpView.getPhoneNumberTextField().setText("");
            signUpView.getMailTextField().setText("");
            signUpView.getBirthdayTextField().setText("");
            signUpView.getGenderButton().clearSelection();
            signUpView.setVisible(true);
        }
        if(e.getSource()==userMainView.getSearchButton()){
            int pDay = Integer.parseInt(userMainView.getPickUpDateDay().getSelectedItem().toString());
            int pMonth = Integer.parseInt(userMainView.getPickUpDateMonth().getSelectedItem().toString());
            int pYear = Integer.parseInt(userMainView.getPickUpDateYear().getSelectedItem().toString());
            int dDay= Integer.parseInt(userMainView.getDeliveryDateDay().getSelectedItem().toString());
            int dMonth= Integer.parseInt(userMainView.getDeliveryDateMonth().getSelectedItem().toString());
            int dYear= Integer.parseInt(userMainView.getDeliveryDateYear().getSelectedItem().toString());
            int passenger;
            if(userMainView.getPassengerAmountComboBox().getSelectedItem().toString().equals("ALL")){
                passenger=-1;
            }
            else{
                 passenger= Integer.parseInt(userMainView.getPassengerAmountComboBox().getSelectedItem().toString());
            }


            String brand=userMainView.getBrandComboBox().getSelectedItem().toString();

            String color=userMainView.getColorComboBox().getSelectedItem().toString();
            String category=userMainView.getCarTypeComboBox().getSelectedItem().toString();
            String gearType=userMainView.getGearTypeComboBox().getSelectedItem().toString();
            LocalDate startDate = LocalDate.of(pYear, pMonth, pDay); // Example start date
            LocalDate returnDate = LocalDate.of(dYear, dMonth, dDay); // Example return date
            if(startDate.isBefore(returnDate)){
                try {
                    System.out.println("--------------------------------");
                    model.showFilteredCars(model.filterCars(brand,category,color,gearType,passenger,pDay,pMonth,pYear,dDay,dMonth,dYear));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else{
                System.out.println("Dates are not valid.");
            }

        }
    }

    public ArrayList<String> getUsersReservations(String username){
        ArrayList<String> data = new ArrayList<>();
        for(int i = 0; i<model.getReservationArrayList().size(); i++){
            Reservation r = model.getReservationArrayList().get(i);
            if(model.getLoggedUserId() == r.getUserId()){
                for(int j = 0; j<model.getVehicleArrayList().size(); j++){
                    Vehicle v = model.getVehicleArrayList().get(j);
                    if(r.getVehicleId() == v.getVehicleId()){
                        data.add(v.getModel());
                        data.add(r.getPickupLocation());
                        data.add(r.getReturnLocation());
                        data.add(r.getPickupDate().toString());
                        data.add(r.getReturnDate().toString());
                    }
                }
            }
        }
        return data;
    }

    public String[][] getData(ArrayList<String> data){
        String[][] dataArray = new String[data.size()/5][5];
        int counter = 0, columnCounter = 0;
        for(int i = 0; i<data.size(); i++){
            dataArray[columnCounter][counter] = data.get(i);
            counter++;
            if(counter % 5 == 0){
                columnCounter++;
                counter = 0;
            }
        }
        return dataArray;
    }
}
