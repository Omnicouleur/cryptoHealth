# cryptoHealth
This project was developed for my master's graduation project, it aims to leverage the  Blockchain technology to secure and protect medical information and records collected from patients.<br/>
This Android application collects health information from a smart band (in our case a Xioami Mi Band 3) and stores it in smart contract deployed on an ethereum based blockchain (Ropsten/Rinkeby).
## Preview
![cryptoHealth](https://media.giphy.com/media/TPI9chE122DC5GEACC/giphy.gif)
  <br/><br/><br/>
## How it works
![cryptoHealthArchitecture](https://i.ibb.co/s9sq7v2/arch-m.png) 
<br/><br/><br/>
1- The user creates an account (email and password)<br/>
2- A wallet will be created for the user and his smart contract will deployed to the network.<br/>
3- The android application automatically scans and connects to nearby Xiaomi Mi 3 bands and sync data.<br/>
4- The user can upload his data to the smart contract any time he wants, or he can set it automatically (steps every 24 hours and Heart Rate every 2 hours)<br/>
5- From his profile, the user can add doctors/coaches in order to give them access to his health records.<br/>
6- Doctors, coaches and other medical staff can access this data through a web application created using NodeJs and Angular 8.<br/>

## Credits
These repositories were a great inspiration and help in developing this project. Thank you guys for your great work.
https://github.com/Freeyourgadget/Gadgetbridge <br/>
https://github.com/Spayker/rn-miband-connector
