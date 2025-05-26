import './Profile.css';

const Profile = ({ name, nameMeaning, numberMeaning}) => (
  <div id="profile">
    <p>Nice to meet ya, {name}! Did you know your name has a recorded background behind it?</p>
    <p className="info">{nameMeaning}</p>
    <p>Crazy! And I bet ya didn't know your favorite number had some analytical sentiment as well!</p>
    <p className="info">{numberMeaning}</p>
    <p>Have a good one pal!</p>
  </div>
);

export default Profile;
