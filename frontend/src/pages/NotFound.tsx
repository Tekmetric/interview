import React from 'react';
import ReactComponent from '../interfaces/ReactChildrenProps';
import { Link } from 'react-router-dom';

const NotFound: React.FC<ReactComponent> = () => {
    return (
      <div content-center>
        Sorry, this page does not exist
        <Link to='/'>Back to home</Link>
      </div>
    );
};

export default NotFound;