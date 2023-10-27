import React, {ReactNode, useEffect} from 'react';
import { useInView, IntersectionOptions } from 'react-intersection-observer';
import {classNames} from "../../utils/Utils";

export interface InViewProps {
   /**
    * Children to render inside InView observer
    */
   children: ReactNode;
   /**
    * className for styling purposes
    */
   className?: string;
   /**
    * onChange callback - returns the value of when the obverser changes inView state
    * @param inView
    */
   onChange: (inView: boolean) => void;
   /**
    * IntersectionOptions
    */
   options?: IntersectionOptions;
};

const InView = (props: InViewProps) => {
   const {children, className = '', options, onChange, ...args} = props;
   const { ref, inView } = useInView({
      initialInView: false,
      threshold: 0.25,
      ...options,
   });

   useEffect(() => {
      if (inView) onChange(inView);
   }, [inView]);

   return (
       <div {...args} ref={ref} className={classNames(className)}>
          {children}
       </div>
   );
};

export default InView;
