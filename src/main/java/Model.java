import Objects.*;
import Objects.Driver;
import View.PaymentInformationView;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model {

    static Connection connection = DBConnection.getConnection();

    private ArrayList<User> userArrayList =new ArrayList<>();
    private ArrayList<Vehicle> vehicleArrayList =new ArrayList<>();
    private ArrayList<Driver> driverArrayList =new ArrayList<>();
    private ArrayList<Extras> extrasArrayList =new ArrayList<>();
    private ArrayList<Reservation> reservationArrayList =new ArrayList<>();
    private ArrayList<ReservationExtras> reservationExtrasArrayList =new ArrayList<>();
    private int loggedUserId;
    private ArrayList<String> promotionCodes = new ArrayList<>();
    private int promotionError=0;

    private int selectedExtrasError =0;

    public Model() throws SQLException {
        getUsersInDB();
        getVehiclesInDB();
        getDriversInDB();
        getExtrasInDB();
        getReservationsInDB();
        getReservationExtrasInDB();
        determinePromotionCodes();
    }

    public void determinePromotionCodes(){
        promotionCodes.add("mcqueen10");
        promotionCodes.add("blackfriday15");
        promotionCodes.add("alonso20");
    }

    public int addUser(User user)  {
        int lastId = 0;
        int userId = 0;

        try {
            Statement statement = connection.createStatement();
            ResultSet rs1 = statement.executeQuery("SELECT MAX(userId) FROM User");

            while (rs1.next()) {
                lastId = rs1.getInt(1);
            }

            if (lastId == 0) {
                userId = 1;
            } else {
                userId = lastId + 1;
            }


            statement.executeUpdate("INSERT INTO User (userId, age, userName, password, name_surname,cardNumber, gender,phoneNumber) VALUES ( '" + userId
                    + "' , '" + user.getAge() + "', '" + user.getUsername() + "' , '" + user.getPassword() + "' , '" + user.getName_surname() + "' , '" + user.getCardNumber()
                    + "' , '" + user.getGender() + "' , '" + user.getPhoneNumber() + "')");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        userArrayList.add(user);
        user.setUserId(userId);
        return userId;
    }

    public boolean signUp(String userName, String password, String passwordCheck, String eMail, String birthday, String phone, String gender) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs1 = statement.executeQuery("SELECT userName FROM User");


        if(checkValidInfoForSignUp(userName, password, passwordCheck, eMail, birthday, phone, gender)==false){
            return false;
        }

        while (rs1.next()) {
            String activeUserName = rs1.getString("userName");
            if(userName.equals(activeUserName)){
                JOptionPane.showMessageDialog(new JFrame(),"--- This username has taken ---","",JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        User newUser=new User(userName, password, userName, eMail, Integer.parseInt(birthday), phone, gender,null);
        addUser(newUser);

        return true;
    }

    public boolean checkValidInfoForSignUp(String userName, String password, String passwordCheck, String eMail, String age, String phone, String gender){

        String errorMessage="";
        int flag =0;
        int ageFlag=0;

        if(age.equals("") || age.contains(" ")){
            ageFlag=1;
        }

        if(userName.equals("") || password.equals("") || passwordCheck.equals("") || eMail.equals("") || age.equals("") || phone.equals("") || gender==null){
            errorMessage+="!! PLEASE FILL ALL THE FIELDS !!\n";
            flag=1;
        }
        if(!password.equals(passwordCheck)){
            errorMessage+="!! PLEASE ENTER THE SAME PASSWORD !!\n";
            flag=1;
        }

        String regex ="^(.+)@(.+)$";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher= pattern.matcher(eMail);
        if(!matcher.matches()){
            errorMessage+="!! PLEASE ENTER A VALID E-MAIL !!\n";
            flag=1;
        }

        if(!isNumeric(age)){
            errorMessage+="!! PLEASE ENTER A VALID AGE !!\n";
            flag=1;
        }else if(Integer.parseInt(age)>100 || Integer.parseInt(age)<18){
            errorMessage+="!! YOU ARE NOT ELIGIBLE (age) !!\n";
            flag=1;
        }
        if(!isNumeric(phone) || !phone.startsWith("05") || phone.length() != 11){
            errorMessage+="!! PLEASE ENTER A VALID PHONE NUMBER !!\n";
            flag=1;
        }
        if(gender==null){
            errorMessage+="!! PLEASE SELECT GENDER !!";
            flag=1;
        }
        if(flag==1) {
            JOptionPane.showMessageDialog(new JFrame(), errorMessage,"SignUp Error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public void getUsersInDB() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT userId, age, userName, password, name_surname, cardNumber, gender, phoneNumber, email FROM user ";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            int userId = resultSet.getInt("userId");
            int age = resultSet.getInt("age");
            String userName = resultSet.getString("userName");
            String password = resultSet.getString("password");
            String name_surname = resultSet.getString("name_surname");
            String cardNumber = resultSet.getString("cardNumber");
            String gender = resultSet.getString("gender");
            String phoneNumber = resultSet.getString("phoneNumber");
            String email = resultSet.getString("email");
            User existingUser = new User(userName, password, name_surname, email, age, phoneNumber, gender, cardNumber);
            existingUser.setUserId(userId);
            userArrayList.add(existingUser);
        }
    }

    public void getVehiclesInDB() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM vehicle ";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            int vehicleId = resultSet.getInt("vehicleId");
            String gearType = resultSet.getString("gearType");
            String color = resultSet.getString("color");
            String carType = resultSet.getString("carType");
            String model = resultSet.getString("model");
            String brand = resultSet.getString("brand");
            boolean isAvailable = resultSet.getBoolean("isAvailable");
            String fuelType = resultSet.getString("fuelType");
            String passengerAmount = resultSet.getString("passengerAmount");
            String dailyPrice = resultSet.getString("dailyPrice");
            Vehicle existingVehicle = new Vehicle(vehicleId, gearType, color, carType, model, brand, isAvailable, fuelType, passengerAmount, dailyPrice);
            vehicleArrayList.add(existingVehicle);
        }
    }

    public void getDriversInDB() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT driverId, age, experienceYear, maxSpeed, isExperienced FROM driver ";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            int driverId = resultSet.getInt("driverId");
            int age = resultSet.getInt("age");
            int experienceYear = resultSet.getInt("experienceYear");
            int maxSpeed = resultSet.getInt("maxSpeed");
            boolean isExperienced = resultSet.getBoolean("isExperienced");
            Driver existingDriver = new Driver(driverId, age, experienceYear, maxSpeed, isExperienced);
            driverArrayList.add(existingDriver);
        }
    }

    public void getExtrasInDB() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT extraId, extraName, price FROM extras ";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            int extraId = resultSet.getInt("extraId");
            String extraName = resultSet.getString("extraName");
            int price = resultSet.getInt("price");
            Extras existingExtras = new Extras(extraId, extraName, price);
            extrasArrayList.add(existingExtras);
        }
    }

    public void getReservationsInDB() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM Reservation ";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            int reservationId = resultSet.getInt("reservationId");
            Date pickupDate = resultSet.getDate("pickupDate");
            Date returnDate = resultSet.getDate("returnDate");
            String pickupLocation = resultSet.getString("pickupLocation");
            String returnLocation = resultSet.getString("returnLocation");
            int resStatus = resultSet.getInt("resStatus");
            int price = resultSet.getInt("price");
            int userId = resultSet.getInt("userId");
            int vehicleId = resultSet.getInt("vehicleId");
            int driverId = resultSet.getInt("driverId");
            Reservation existingReservation = new Reservation(reservationId, pickupDate, returnDate, pickupLocation, returnLocation, resStatus, price, userId, vehicleId);
            existingReservation.setDriverId(driverId);
            reservationArrayList.add(existingReservation);
        }
    }

    public void getReservationExtrasInDB() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM reservationExtras ";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            int reservationId = resultSet.getInt("reservationId");
            int extraId = resultSet.getInt("extraId");
            ReservationExtras existingReservationExtras = new ReservationExtras(reservationId, extraId);
            reservationExtrasArrayList.add(existingReservationExtras);
        }
    }

    public boolean logIn(String username, String password) throws SQLException {
        Statement statement = connection.createStatement();
        if(username.equals("") || password.equals("")){
            JOptionPane.showMessageDialog(new JFrame(),"PLEASE FILL BOTH FIELDS !!!","Login Error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        ResultSet rs1 = statement.executeQuery("SELECT username, userId " +
                "FROM user " +
                        "WHERE username='" + username + "' and password= '" + password +"';");

        if (rs1.next()) {
            loggedUserId = rs1.getInt("userId");
            return true;
        } else {
            JOptionPane.showMessageDialog(new JFrame(),"WRONG USERNAME OR PASSWORD !!!","Login Error",JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }


    public int addReservation(Date pickupDate, Date returnDate, String pickupLocation, String returnLocation, int resStatus, double price, int userId, int vehicleId){
        int lastId = 0;
        int reservationId = 0;

        try {
            Statement statement = connection.createStatement();
            ResultSet rs1 = statement.executeQuery("SELECT MAX(reservationId) FROM Reservation");

            while (rs1.next()) {
                lastId = rs1.getInt(1);
            }

            if (lastId == 0) {
                reservationId = 1;
            } else {
                reservationId = lastId + 1;
            }


            statement.executeUpdate("INSERT INTO Reservation (reservationId, pickupDate, returnDate, pickupLocation, returnLocation ,resStatus , price , userId , vehicleId , driverId) VALUES ( '" + reservationId
                    + "' , '" + pickupDate + "', '" + returnDate + "' , '" + pickupLocation+ "' , '" + returnLocation + "' , '" + resStatus
                    + "' , '" + price + "' , '" + userId + "','" +vehicleId + "',null)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Reservation reservation = new Reservation(reservationId,pickupDate,returnDate,pickupLocation,returnLocation,resStatus,(int) price,userId,vehicleId);
        reservationArrayList.add(reservation);
        return reservationId;

    }

    public void addExtrastoReservation(int reservationId,ArrayList<Integer> extraId) throws SQLException {
        Statement statement = connection.createStatement();
        for(int i=0;i<extraId.size();i++){
            statement.executeUpdate("INSERT INTO reservationExtras (reservationId, extraId) VALUES ( '" + reservationId
                    + "' , '"  +extraId.get(i) + "')");
        }
    }
    public void assignDriver(int driverId, int reservationId) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE Reservation SET driverId =" + driverId + " WHERE reservationId =" + reservationId);
        statement.close();
    }

    public void addPaymentInformation(String cardNumber, String cvv, String lastDateYear, String lastDateMonth, String lastDateDay, User user) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE user SET cardNumber=" + cardNumber + ",cvv=" + cvv + ",lastDateYear=" + lastDateYear + ",lastDateMonth=" + lastDateMonth + ",lastDateDay=" + lastDateDay + " WHERE userId= " + user.getUserId());

        for(int i = 0; i<userArrayList.size(); i++){
            if(userArrayList.get(i).getUserId() == user.getUserId()){
                userArrayList.get(i).setCvv(cvv);
                userArrayList.get(i).setLastDateYear(lastDateYear);
                userArrayList.get(i).setLastDateMonth(lastDateMonth);
                userArrayList.get(i).setLastDateDay(lastDateDay);
            }
        }
    }


    public ResultSet filterCars(String brand, String category, String color, String gearType, String fuelType, int passenger,Date pickupDate, Date deliverDate) throws SQLException {
        Statement statement = connection.createStatement();
        StringBuilder queryBuilder = new StringBuilder("SELECT V.vehicleId, V.gearType, V.color, V.carType, V.model, V.brand, V.isAvailable, V.fuelType, V.passengerAmount, V.dailyPrice, V.fuelType FROM Vehicle V WHERE V.vehicleId NOT IN(SELECT R.vehicleId FROM Reservation R WHERE R.pickupDate <= '" + deliverDate + "' AND R.returnDate >= '" + pickupDate + "')" );
        if (!"ALL".equals(brand)) {
            queryBuilder.append(" AND V.brand = '").append(brand).append("'");
        }
        if (!"ALL".equals(gearType)) {
            queryBuilder.append(" AND V.gearType = '").append(gearType).append("'");
        }
        if (!"ALL".equals(category)) {
            queryBuilder.append(" AND V.carType = '").append(category).append("'");
        }
        if (!"ALL".equals(color)) {
            queryBuilder.append(" AND V.color = '").append(color).append("'");
        }
        if (!"ALL".equals(fuelType)) {
            queryBuilder.append(" AND V.fuelType = '").append(fuelType).append("'");
        }
        if (passenger != -1) {
            queryBuilder.append(" AND V.passengerAmount = ").append(passenger);
        }
        ResultSet rs1 = statement.executeQuery(queryBuilder.toString());
        return rs1;
    }
    public ArrayList<Vehicle> showFilteredCars(ResultSet rs1) throws SQLException {
        ArrayList<Vehicle> cars = new ArrayList<>();
        while(rs1.next()){
            int vehicleId = rs1.getInt("vehicleId");
            String gearType = rs1.getString("gearType");
            String color = rs1.getString("color");
            String carType = rs1.getString("carType");
            String model = rs1.getString("model");
            String brand = rs1.getString("brand");
            boolean isAvailable = rs1.getBoolean("isAvailable");
            String fuelType = rs1.getString("fuelType");
            String passengerAmount = rs1.getString("passengerAmount");
            String dailyPrice = rs1.getString("dailyPrice");
            cars.add(new Vehicle(vehicleId, gearType, color, carType, model, brand, isAvailable, fuelType, passengerAmount, dailyPrice));
        }
        return cars;
    }

    public boolean paymentValidation(String nameOnTheCard, String cardNumber, String cvv, String promotionCode){

        String cardName = nameOnTheCard;
        cardName = cardName.replaceAll("\\s+","") ;
        promotionCode = promotionCode.toLowerCase();

        String errorMessage="";
        int flag=0;

        if(nameOnTheCard.equals("") || cardNumber.equals("") || cvv.equals("")){
            errorMessage += "!! PLEASE FILL ALL REQUIRED FIELDS !!\n";
            flag=1;
        }else {
            if (checkStringIfItIsFullOfLetters(cardName) == false) {
                errorMessage += "!! PLEASE ENTER ONLY LETTERS IN NAME FIELD !!\n";
                flag = 1;
            }
            if (!isNumeric(cardNumber)) {
                errorMessage += "!! PLEASE ENTER ONLY INTEGERS IN CARD NUMBER FIELD !!\n";
                flag = 1;
            } else if (cardNumber.length() != 16) {
                errorMessage += "!! INVALID CARD NUMBER (NEED 16 DIGITS) !!\n";
                flag = 1;
            }
            if (!isNumeric(cvv)) {
                errorMessage += "!! PLEASE ENTER ONLY INTEGERS IN CCV !!\n";
                flag = 1;
            } else if (cvv.length() != 3) {
                errorMessage += "!! INVALID CCV (NEED 3 DIGITS) !!\n";
                flag = 1;
            }

        }

        if(flag==1){
            JOptionPane.showMessageDialog(new JFrame(), errorMessage,"VALIDATION ERROR",JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public boolean promotionValidation(String promotionCode){
        if (!promotionCodes.contains(promotionCode) && !promotionCode.equals("")) {
            promotionError=1;
            JOptionPane.showMessageDialog(new JFrame(),"!! Invalid Promotion Code !!","",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public int applyPromotion(PaymentInformationView paymentInformationView){

        String enteredCode = paymentInformationView.getPromotionCodeTextField().getText().toLowerCase();
        int discountpPercentage = 0;

        if(enteredCode.equals(promotionCodes.get(0))){
            paymentInformationView.getDiscountTextfield().setText("10%");
            discountpPercentage = 10;
        }else if(enteredCode.equals(promotionCodes.get(1))) {
            paymentInformationView.getDiscountTextfield().setText("15%");
            discountpPercentage = 15;
        }else if(enteredCode.equals(promotionCodes.get(2))) {
            paymentInformationView.getDiscountTextfield().setText("20%");
            discountpPercentage = 20;
        }
        if(discountpPercentage==0){
            JOptionPane.showMessageDialog(new JFrame(),"!! Please enter a promotion code if you have one !!");
        }else {
            JOptionPane.showMessageDialog(new JFrame(), "** " + discountpPercentage + "% discount has been applied **");
        }
        return discountpPercentage;
    }

    public void updateTotalCharge(PaymentInformationView paymentInformationView){

        int discountPercentage = applyPromotion(paymentInformationView);
        double totalAmount = Double.parseDouble(paymentInformationView.getTotalTextField().getText());

        totalAmount = totalAmount - totalAmount*discountPercentage/100;

        paymentInformationView.getTotalTextField().setText(Double.toString(totalAmount));
    }

    public boolean checkStringIfItIsFullOfLetters(String string){
        for(char c : string.toCharArray()){
            if(!Character.isLetter(c)){
                return false;
            }
        }
        return true;
    }
    public int validateDriver(Date pickupDate, Date deliverDate, boolean isExperienced) throws SQLException {
        int i;
        if(isExperienced)
            i=1;
        else{
            i=0;
        }
        ResultSet rs1;
        Statement statement = connection.createStatement();
        rs1=statement.executeQuery( "SELECT DISTINCT D.driverId " + "FROM Driver D " + "LEFT JOIN Reservation R ON D.driverId = R.driverId " + "AND R.pickupDate <= '" + deliverDate + "' " + "AND R.returnDate >= '" + pickupDate + "' " +  "WHERE R.driverId IS NULL " +   "AND D.isExperienced = '" + i + "' " + "ORDER BY D.driverId ASC");
        if(rs1.next()){
            return rs1.getInt("driverId");
        }
        return -1;
    }

    public boolean extrasValidation(String selectedDriver, String selectedDriverPreference, String selectedSeat, String selectedTireChain, String selectedRoofBox, String selectedProtection){

        String errorMessage ="";

        if(selectedTireChain==null || selectedRoofBox==null || selectedProtection==null){
            selectedExtrasError =1;
            errorMessage += "!! PLEASE FILL EXTRAS PART !!\n";
        }
        if(selectedDriver!=null){
            if(selectedDriver.equals("Yes") && selectedDriverPreference==null){
                errorMessage += "!! PLEASE SELECT DRIVER TYPE !!\n";
                selectedExtrasError=1;
            }
            if(selectedDriver.equals("No") && selectedDriverPreference!=null){
                errorMessage += "DON'T SELECT PREFERENCE IF NO NEED DRIVER\n";
            }
        }else{
            errorMessage += "!! PLEASE FILL DRIVER PART !!\n";
        }

        if(!(errorMessage.isEmpty())){
            JOptionPane.showMessageDialog(new JFrame(),errorMessage,"",JOptionPane.ERROR_MESSAGE);
            selectedExtrasError=1;
            return false;
        }
        return true;
    }

    public int calculateExtraPayment(String selectedDriver, String selectedDriverPreference, String selectedSeat, String selectedTireChain, String selectedRoofBox, String selectedProtection){
        int extraAmount=0;

        if(selectedDriver.equals("Yes")){
            if(selectedDriverPreference.equals("Normal")){
                 extraAmount += 500;
            }else if(selectedDriverPreference.equals("Experienced")){
                extraAmount += 750;
            }
        }

        if(selectedSeat != null) {
            if (selectedSeat.equals("Child")) {
                extraAmount += 50;
            } else if (selectedSeat.equals("Baby")) {
                extraAmount += 30;
            }
        }

        if(selectedTireChain.equals("Yes")){
            extraAmount += 10;
        }

        if(selectedRoofBox.equals("Yes")){
            extraAmount += 40;
        }

        if(selectedProtection.equals("High")){
            extraAmount += 200;
        }else if(selectedProtection.equals("Medium")){
            extraAmount += 150;
        }else if(selectedProtection.equals("Additional")){
            extraAmount += 100;
        }

    return extraAmount;
    }


    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public ArrayList<Vehicle> getVehicleArrayList() {
        return vehicleArrayList;
    }

    public ArrayList<Reservation> getReservationArrayList() {
        return reservationArrayList;
    }

    public ArrayList<ReservationExtras> getReservationExtrasArrayList() {
        return reservationExtrasArrayList;
    }

    public int getLoggedUserId() {
        return loggedUserId;
    }

    public int getPromotionError() {
        return promotionError;
    }

    public int getSelectedExtrasError() {
        return selectedExtrasError;
    }

}
