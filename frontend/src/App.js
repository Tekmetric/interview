import { useState } from 'react';
import FormContainer from './components/FormContainer';
import Profile from './components/Profile';
import { containsOnlyLetters, containsOnlyNumbers, fetchNameMeaning, fetchNumberMeaning, handleInputFieldSubmit, isEnterPressed } from './utils';

const App = () => {
  const [formStep, setFormStep] = useState(0);
  
  const [name, setName] = useState('');
  const [nameMeaning, setNameMeaning] = useState('');
  const [numberMeaning, setNumberMeaning] = useState('');

  const tryNextStep = (e) => {
    if (isEnterPressed(e)) {
      setFormStep(formStep + 1);
      return true;
    }

    return false;
  }

  const handleNameMeaningSubmit = (e) => {
    const inputValue = e.target.value;
    if (!containsOnlyLetters(inputValue)) {
      alert("Beep, boop! No l33t names or spaces please :)");
      e.target.value = '';
      return;
    }
    tryNextStep(e) && handleInputFieldSubmit(inputValue, setName, setNameMeaning, fetchNameMeaning);
  }

  const handleNumberMeaningSubmit = (e) => {
    const inputValue = e.target.value;
    console.log()
    if (!containsOnlyNumbers(inputValue)) {
      alert("Beep, boop! No crazy algebraic numbers or spaces please :)");
      e.target.value = '';
      return;
    }
    tryNextStep(e) && handleInputFieldSubmit(inputValue, () => {}, setNumberMeaning, fetchNumberMeaning);
  }

  const InputNameField = () => (
    <div className='input'>
      <label>Howdy partner, what's yer name?</label>
      <input type="text" pattern="[A-Za-z]" placeholder="John" autoFocus onKeyUp={handleNameMeaningSubmit} />
    </div>
  );

  const InputNumberField = () => (
    <div className='input'>
      <label>Nice to meet ya, {name}! What's yer FAVORITE number?</label>
      <input type="text" inputMode="numeric" placeholder="123" pattern="[0-9]" maxLength="3" autoFocus onKeyUp={handleNumberMeaningSubmit} />
    </div>
  );

  const formSteps = [
    <InputNameField />,
    <InputNumberField />,
    <Profile 
      name={name}
      nameMeaning={nameMeaning}
      numberMeaning={numberMeaning}
    />
  ];

  return <FormContainer formStep={formStep} formSteps={formSteps} />;
}

export default App;
