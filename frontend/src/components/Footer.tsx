import GitHubIcon from '@mui/icons-material/GitHub';
import InstagramIcon from '@mui/icons-material/Instagram';
import LinkedInIcon from '@mui/icons-material/LinkedIn';

const Footer = () => (
  <div className="flex flex-col gap-y-3 justify-center items-center w-screen bg-black max-h-fit py-6">
    <div className="text-white text-xl">Radu Baston</div>
    <div className="flex gap-x-5 justify-center item-center">
      <GitHubIcon sx={{ color: 'white' }} />
      <InstagramIcon sx={{ color: 'white' }} />
      <LinkedInIcon sx={{ color: 'white' }} />
    </div>
  </div>
);

export default Footer;
