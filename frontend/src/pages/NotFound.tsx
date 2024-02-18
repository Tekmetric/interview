import React from 'react';
import { Link } from 'react-router-dom';

import { ReactComponent } from '../interfaces/components';

const NotFound: React.FC<ReactComponent> = () => {
    return (
      <div content-center>
        Sorry, this page does not exist
        <Link to='/'>Back to home</Link>
      </div>
    );
};

export default NotFound;