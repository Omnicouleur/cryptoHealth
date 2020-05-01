pragma solidity >=0.4.22 <0.7.0;

contract eHealth  {

    /* Define variable owner of the type address */
    address public owner;
    // Health info stores all the data retrieved from the smartband
    struct HealthInfo {
        string date;
        string heartBeat;
        string steps;
        string calories;
    }
    // healthInfos stores the patients health data
    HealthInfo[] healthInfos;
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
    function addHealthInfos(string memory _date,
                            string memory _heartBeat,
                            string memory _steps,
                            string memory _calories) public {
        //require(patients[msg.sender]);
        HealthInfo memory healthInfo = HealthInfo({
                                    date : _date,
                                    heartBeat : _heartBeat,
                                    steps : _steps,
                                    calories : _calories
                                   });
        healthInfos.push(healthInfo);
           }
    /* check healthInfo of a certain patient  */
    function checkHealthInfos() public view returns (string memory) {
        string memory result = '';
        if (msg.sender == owner)  {result = getHealthInfosForPatient();}
        else if (coachs[msg.sender]) {result = getHealthInfosForCoach() ;}
        else if (doctors[msg.sender]) {result = getHealthInfosForDoctor();}
        else {revert("Unauthorized access"); }
        return result;
    }
     function getHealthInfosForPatient() private view returns (string memory) {
         string memory result = '';
        uint arrayLength = healthInfos.length;
        for (uint i = 0; i<arrayLength; i++){
        result = append(result,healthInfos[i].date,"|&|",healthInfos[i].heartBeat,"|&|");
        result = append(result,healthInfos[i].steps,"|&|", 
                        healthInfos[i].calories,"|#|");
        }
     } 
    function getHealthInfosForDoctor() private view returns (string memory) {
        string memory result = '';
        uint arrayLength = healthInfos.length;
        for (uint i = 0; i<arrayLength; i++){
        result = append(result,healthInfos[i].date,"|&|",healthInfos[i].heartBeat,"|&|");
        result = append(result,healthInfos[i].steps,"|&|", 
                        healthInfos[i].calories,"|#|");
        }
     }   
    function getHealthInfosForCoach() private view returns (string memory) {
         string memory result = '';
        uint arrayLength = healthInfos.length;
        for (uint i = 0; i<arrayLength; i++){
        result = append(result,healthInfos[i].date,"|&|","","");
        result = append(result,healthInfos[i].steps,"|&|", 
                        healthInfos[i].calories,"|#|");
        }
     }   
    function append(string memory a, string memory b, string memory c, string memory d, string memory e) internal pure returns (string memory) {
        return string(abi.encodePacked(a, b, c, d, e));
    }
}