pragma solidity >=0.4.22 <0.7.0;

contract eHealth  {

    /* Define variable owner of the type address */
    address public owner;
    // Health info stores all the data retrieved from the smartband
    struct HealthInfo {
        string date;
        string steps;
        string calories;
    }    
    // Health info stores all the data retrieved from the smartband
    struct HeartRateInfo {
        string date;
        string heartRate;
    }
    // stepsInfos stores the patients health data
    HealthInfo[] stepsInfos;
    HeartRateInfo[] heartRateInfos;
    enum Gender {male,female}

    mapping(address => bool) private doctors;
    mapping(address => bool) private coachs;

    /* This runs when the contract is executed */
    constructor() public {
	owner = msg.sender;
    }

    function addDoctor(address doctor) public {
        doctors[doctor] = true;
        //doctors.push(doctor);
    }    
    function addCoach(address coach) public {
        coachs[coach] = true;
        //coachs.push(coach);
    }    
    function deleteDoctor(address doctor) public {
        doctors[doctor] = false;
    }
    function deleteCoach(address coach) public {
        coachs[coach] = false;
    }

    /* Add new healthInfo of a certain patient */
    function addStepsInfos(string memory _date,
                            string memory _steps,
                            string memory _calories) public {
        //require(patients[msg.sender]);
        HealthInfo memory healthInfo = HealthInfo({
                                    date : _date,
                                    steps : _steps,
                                    calories : _calories
                                   });
        stepsInfos.push(healthInfo);
           }
    /* Add new healthInfo of a certain patient */
    function addHeartRate(string memory _date, string memory _heartRate) public {
        //require(patients[msg.sender]);
        HeartRateInfo memory heartRateInfo = HeartRateInfo({
                                    date : _date,
                                    heartRate : _heartRate
                                   });
        heartRateInfos.push(heartRateInfo);
           }
    /* check healthInfo of a certain patient  */
    function checkStepsInfos() public view returns (string memory) {
        string memory result = "Hello";
        if (msg.sender == owner)  {result = getStepsInfosForPatient();}
        else if (coachs[msg.sender]) {result = getStepsInfosForCoach() ;}
        else if (doctors[msg.sender]) {result = getStepsInfosForDoctor();}
        else {revert("Unauthorized access"); }
        return result;
    }
        /* check healthInfo of a certain patient  */
    function checkHeartRateInfos() public view returns (string memory) {
        string memory result = "Hello";
        if ((msg.sender == owner) || (doctors[msg.sender]))  {result = getHeartRateData();}
        else {revert("Unauthorized access"); }
        return result;
    }
    function getPatientName() public pure returns (string memory) {
        return "patients[msg.sender].name";
    }
    
    function getStepsInfosForPatient() private view returns (string memory) {
         string memory result = '';
        uint arrayLength = stepsInfos.length;
        for (uint i = 0; i<arrayLength; i++){
        result = append(result,stepsInfos[i].date,"|&|","","");
        result = append(result,stepsInfos[i].steps,"|&|", 
                        stepsInfos[i].calories,"|#|");
        }
        return result;
     } 
    function getHeartRateData() private view returns (string memory) {
         string memory result = '';
        uint arrayLength = heartRateInfos.length;
        for (uint i = 0; i<arrayLength; i++){
        result = append(result,heartRateInfos[i].date,"|&|",heartRateInfos[i].heartRate,"|#|");
        }
        return result;
     } 
    function getStepsInfosForDoctor() private view returns (string memory) {
        string memory result = '';
        uint arrayLength = stepsInfos.length;
        for (uint i = 0; i<arrayLength; i++){
        result = append(result,stepsInfos[i].date,"|&|","","");
        result = append(result,stepsInfos[i].steps,"|&|", 
                        stepsInfos[i].calories,"|#|");
        }
        return result;
     }   
    function getStepsInfosForCoach() private view returns (string memory) {
         string memory result = '';
        uint arrayLength = stepsInfos.length;
        for (uint i = 0; i<arrayLength; i++){
        result = append(result,stepsInfos[i].date,"|&|","","");
        result = append(result,stepsInfos[i].steps,"|&|", 
                        stepsInfos[i].calories,"|#|");
        }
        return result;
     }   
    function append(string memory a, string memory b, string memory c, string memory d, string memory e) internal pure returns (string memory) {
        return string(abi.encodePacked(a, b, c, d, e));
    }
}