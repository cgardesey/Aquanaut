# Water ATM App

Water ATM App is an Android application designed to provide a seamless and convenient way for clients to sell water to users with a vending machine using Bluetooth technology. This app allows clients to interact with the vending machine, make payments, and dispense water for users with just a few taps on their smartphone.


## Features

This project includes the following features:

- **Bluetooth Connectivity**: Connects to the water vending machine via Bluetooth for secure and efficient communication.

- **Balance Topup**: Each user has and RFID tag. Clients topup balance on users' RFID tag. On the user screen, client places RFID tag on embedded device to launch topup material dialog for balance topup.

- **Register Tag**: Allows clients to register new tags for their users. On the user tag list screen, user places RFID tag on embedded device to launch *Register Tag* material dialog to register new tags for users.
  
- **user Registration**: Allows clents to register new users and update user details.

## Screenshots
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/ec2a2c6d-7592-486a-8fd4-98f495b18472" width="250" alt="Screenshot 1">
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/6a140061-f064-4fa4-8319-0e1ebf64064e" width="250" alt="Screenshot 2">
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/6ca2356e-edaf-4fb6-97d6-7a651d7496ac" width="250" alt="Screenshot 3">
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/47857b1c-5dd7-45c1-ab39-0f832959c270" width="250" alt="Screenshot 4">
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/a8bc4802-9cb9-4d17-92a4-68f19d503b33" width="250" alt="Screenshot 5">
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/a9fbe2f8-0836-4dc7-8c03-2a221fc94b73" width="250" alt="Screenshot 6">
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/ab38b6c4-7f08-47ba-af0a-f47be031e089" width="250" alt="Screenshot 7">
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/ac37f21c-b5be-4b67-a0cf-0d4875d28484" width="250" alt="Screenshot 8">
<img src="https://github.com/cgardesey/Aquanaut/assets/10109354/2a5d48ad-fc14-447f-bdb9-debe7e6d6603" width="250" alt="Screenshot 8">

## Getting Started

To get started with the project, follow the instructions below:

1. Clone this repository to your local machine using Git or download the ZIP file.
2. Open Android Studio and select "Open an existing Android Studio project."
3. Navigate to the cloned or downloaded project directory and click "OK."
4. Android Studio will build the project and download any necessary dependencies.
5. Connect an Android device or use an emulator to run the application.

## Configuration

The example project requires the following configuration:

- Minimum SDK version: 14
- Target SDK version: 23

You may need to update these values in the `build.gradle` file based on your project requirements.

## Usage

1. Open the project in Android Studio.
2. Connect your Android device to your computer via USB.
3. Build and run the app on your Android device.
   
If you're looking to integrate with arduino project, make sure to check out the the repository corresponding to the [ariduino firmware](https://github.com/cgardesey/remote_water_level_measurement_firmware) for detailed instructions.

## License

RemoteWaterMonitoring project is licensed under the [MIT License](https://opensource.org/licenses/MIT). Feel free to use it as a reference or starting point for your own projects.
