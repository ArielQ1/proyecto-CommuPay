import logo from '../assets/commuPayLogo.png'

export function NavBar () {
  return (
    <nav className='bg-gray-950 p-3 border-b border-gray-500'>
      <div className='container mx-auto flex justify-between items-center'>
        <div className='flex items-center space-x-4'>
          <img src={logo} className='w-14 h-14' alt='' />
          <div className='text-white text-lg font-bold'>CommuPay</div>
        </div>
        <div className='flex items-center space-x-6'>
          <a href='/' className='text-gray-300 hover:text-white px-3'>Home</a>
          <a href='/profile' className='text-gray-300 hover:text-white px-3'>Profile</a>
          <a href='/events' className='text-gray-300 hover:text-white px-3'>Events</a>
          <img src={logo} className='w-14 h-14 rounded-full bg-amber-50' alt='picture of ' />
        </div>
      </div>
    </nav>
  )
}
