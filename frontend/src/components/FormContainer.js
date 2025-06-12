import { useRef} from 'react';
import { SwitchTransition, CSSTransition } from "react-transition-group";
import './FormContainer.css';

const FormContainer = ({ formStep, formSteps }) => {
  const refs = Array(formSteps.length).fill(useRef(null));
  const nodeRef = refs[formStep];

  return (
    <div id="container">
      <SwitchTransition mode={"out-in"}>
        <CSSTransition
          key={formStep}
          nodeRef={nodeRef}
          addEndListener={(done) => {
            nodeRef.current.addEventListener("transitionend", done, false);
          }}
          classNames="fade"
        >
          <div ref={nodeRef} className="input-container">
            {formSteps.at(formStep)}
          </div>
        </CSSTransition>
      </SwitchTransition>
    </div>
  )
}

export default FormContainer;