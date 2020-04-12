pragma solidity >=0.4.22 <0.7.0;

contract eHealth  {
    string greeting;
    /* Define variable owner of the type address */
    address public owner;
    // Health info stores all the data retrieved from the smartband
    struct HealthInfo {
        string date;
        uint heartBeat;
        uint o2Level;
        uint calories;
    }
    // This declares a state variable that
    // stores a `healthInfo` struct for each possible address.
    mapping(address => HealthInfo[]) public healthInfoMap;
    enum Gender {male,female}
    // this struct holds the patient infos and any possible relation to a doctor/coach
    struct Patient {
        address pbkey;
        string name;
        address[] doctor;
        address coach;
        uint age;
        Gender gender;
    }

    mapping(address => Patient) public patients;

    enum Job {coach,doctor}

    struct Staff {
        string name;
        address pbkey;
        address[] patients;
        Job job;
    }
    address[] users;
    /* This runs when the contract is executed */
    constructor() public {
	owner = msg.sender;
    }

    /* create a new patient  */
    function createPatient( string memory _name, uint _age, uint _gender) public {
        address ie;
        address[] memory ieTab;
        Patient memory _patient = Patient({pbkey : msg.sender,
                                    name : _name,
                                    doctor : ieTab,
                                    coach : ie,
                                    age : _age,
                                    gender: _gender == 0 ? Gender.male : Gender.female
        });
        patients[msg.sender] = _patient;
    }
     /* update a patient info */
    function updatePatient(string memory _greeting) public {
        greeting = _greeting;
    }
    /* create a new staff member (doctor or coach)  */
    function createStaff(string memory _greeting) public {
        require (msg.sender == owner,"Staff members can only be created by the admin");
        greeting = _greeting;
    }
     /* update a staff member (doctor or coach)  */
    function updateStaff(string memory _greeting) public {
        greeting = _greeting;
    //check : does it update staff ? or create a new one ?
    }
    /* Add new healthInfo of a certain patient */
    function addHealthInfos(string memory _date,
                            uint _heartBeat,
                            uint _o2Level,
                            uint _calories) public {
        //require(patients[msg.sender]);
        HealthInfo memory healthInfo = HealthInfo({
                                    date : _date,
                                    heartBeat : _heartBeat,
                                    o2Level : _o2Level,
                                    calories : _calories
                                   });
        healthInfoMap[msg.sender].push(healthInfo);
           }
    /* check healthInfo of a certain patient  */
    function checkHealthInfos() public view returns (string memory) {
        string memory result = '';
        uint arrayLength = healthInfoMap[msg.sender].length;

        for (uint i = 0; i<arrayLength; i++){
        result = append(result,healthInfoMap[msg.sender][i].date,"|&|",convertIntToString(healthInfoMap[msg.sender][i].heartBeat),"|&|");
        result = append(result,convertIntToString(healthInfoMap[msg.sender][i].o2Level),"|&|",
        convertIntToString(healthInfoMap[msg.sender][i].calories),"|#|");
        }
        return result;
    }

    function append(string memory a, string memory b, string memory c, string memory d, string memory e) internal pure returns (string memory) {
        return string(abi.encodePacked(a, b, c, d, e));
    }

    function convertIntToString(uint256 _number) internal pure returns (string memory) {
        uint256 _tmpN = _number;

        if (_tmpN == 0) {
            return "0";
        }
        uint256 j = _tmpN;
        uint256 length = 0;

        while (j != 0){
            length++;
            j /= 10;
        }

        bytes memory bstr = new bytes(length);
        uint256 k = length - 1;

        while (_tmpN != 0) {
            bstr[k--] = byte(uint8(48 + _tmpN % 10));
            _tmpN /= 10;
        }
        return string(bstr);
}
    /* Main function */
    function getPatientName() public view returns (string memory) {
        return patients[msg.sender].name;
    }
    /* Main function */
    function getFaza() public pure returns (string memory, string memory) {
        return ("1st","2nd");
    }
}
