<?php


class CaptchasDotNet
{
  function CaptchasDotNet ($client, $secret,
                           $random_repository = '/tmp/captchasnet-random-strings',
                           $cleanup_time      = 3600,
                           $alphabet          = 'abcdefghijklmnopqrstuvwxyz',
                           $letters           = 6,
                           $width             = 240,
                           $height            = 80
                           )
  {
    $this->__client = $client;
    $this->__secret = $secret;
    $this->__random_repository = $random_repository;
    $this->__cleanup_time      = $cleanup_time;
    $this->__time_stamp_file   = $random_repository . '/__time_stamp__';
    $this->__alphabet          = $alphabet;
    $this->__letters           = $letters;
    $this->__width             = $width;
    $this->__height            = $height;
  }

  function __random_string ()
  {
    // The random string shall consist of small letters, big letters
    // and digits.
    $letters = "abcdefghijklmnopqrstuvwxyz";
    $letters .= strtoupper ($letters) + "0123456789";

    // The random starts out empty, then 40 random possible characters
    // are appended.
    $random_string = '';
    for ($i = 0; $i < 40; $i++)
    {
      $random_string .= $letters{rand (0, strlen ($letters) - 1)};
    }

    // Return the random string.
    return $random_string;
  }

  // Create a new random string and register it.
  function random ()
  {
    // If the repository directory is does not yet exist, create it.
    if (!is_dir ($this->__random_repository))
    {
      mkdir ($this->__random_repository);
    }

    // If the time stamp file does not yet exist, create it.
    if (!is_file ($this->__time_stamp_file))
    {
      touch ($this->__time_stamp_file);
    }

    // Get the current time.
    $now = time ();

    // Determine the time, before which to remove random strings.
    $cleanup_time = $now - $this->__cleanup_time;

    // If the last cleanup is older than specified, cleanup the
    // directory.
    if (filemtime ($this->__time_stamp_file) < $cleanup_time)
    {
      $handle = opendir ($this->__random_repository);
      while (true)
      {
        $filename = readdir ($handle);
        if (!$filename)
        {
          break;
        }
        if ($filename != '.' && $filename != '..')
        {
          $filename = $this->__random_repository . '/' . $filename;
          if (filemtime ($filename) <  $cleanup_time)
          {
            unlink ($filename);
          }
        }
      }
      closedir ($handle);

      touch ($this->__time_stamp_file);
    }

    // loop until a valid random string has been found and registered,
    // but at most 20 times. If no valid random has been found during
    // that time, there is something really wrong. Also show the error
    // in the last run.
    for ($remaining = 20; $remaining > 0; $remaining--)
    {
      // generate a new random string.
      $random = $this->__random_string ();

      // open a file with the corresponding name in the repository
      // directory in such a way, that the creation fails, when the
      // file already exists. That should be near to impossible with
      // good seeding of the random number generator, but it's better
      // to play safe. If this is the last run, show the possible
      // error message.
      $filename = $this->__random_repository . '/' . $random;

      if ($remaining == 1)
      {
        $file = fopen ($filename, 'x');
      }
      else
      {
        $file = @fopen ($filename, 'x');
      }

      if ($file)
      {
        fclose ($file);
        break;
      }

      // if the file already existed, rerun the loop to try the next
      // string.
    }

    // return the successfully registered random string.
    $this->__random = $random;
    return $random;
  }

  //
  // Generates image-URL Parameters are only atached if different from default
  //
  function image_url ($random = False, $base = 'http://image.captchas.net/')
  {
    if (!$random)
    {
      $random = $this->__random;
    }
    $image_url  = $base;
    $image_url .= '?client='   . $this->__client;
    $image_url .= '&amp;random='   . $random;
    if ($this->__alphabet!='abcdefghijklmnopqrstuvwxyz') {$image_url .= '&amp;alphabet=' . $this->__alphabet;};
    if ($this->__letters!=6) {$image_url .= '&amp;letters='  . $this->__letters;};
    if ($this->__width!=240) {$image_url .= '&amp;width='    . $this->__width;};
    if ($this->__height!=80) {$image_url .= '&amp;height='   . $this->__height;};
    return $image_url;
  }

  //
  // Same as image_url but without width and height
  //
  function audio_url ($random = False, $base = 'http://audio.captchas.net/')
  {
    if (!$random)
    {
      $random = $this->__random;
    }
    $audio_url  = $base;
    $audio_url .= '?client='   . $this->__client;
    $audio_url .= '&amp;random='   . $random;
    if ($this->__alphabet!='abcdefghijklmnopqrstuvwxyz') {$audio_url .= '&amp;alphabet=' . $this->__alphabet;};
    if ($this->__letters!=6) {$audio_url .= '&amp;letters='  . $this->__letters;};
    return $audio_url;
  }

  //
  // Generates complete html-sample with javascript to reload image from
  // backup server
  //
  function image ($random = False, $id = 'captchas.net')
  {
    $image = <<<EOT
        <a href="http://captchas.net"><img
            style="border: none; vertical-align: bottom"
            id="@ID@" src="@URL@" width="@WIDTH@" height="@HEIGHT@"
            alt="The Captcha image" /></a>
        <script type="text/javascript">
          <!--
          function captchas_image_error (image)
          {
            if (!image.timeout) return true;
            image.src = image.src.replace (/^http:\/\/image\.captchas\.net/,
                                           'http://image.backup.captchas.net');
            return captchas_image_loaded (image);
          }

          function captchas_image_loaded (image)
          {
            if (!image.timeout) return true;
            window.clearTimeout (image.timeout);
            image.timeout = false;
            return true;
          }

          var image = document.getElementById ('@ID@');
          image.onerror = function() {return captchas_image_error (image);};
          image.onload = function() {return captchas_image_loaded (image);};
          image.timeout
            = window.setTimeout(
               "captchas_image_error (document.getElementById ('@ID@'))",
               10000);
          image.src = image.src;
          //-->
        </script>
EOT;
    $image = str_replace ('@HEIGHT@', $this->__height, $image);
    $image = str_replace ('@WIDTH@', $this->__width, $image);
    $image = str_replace ('@ID@', $id, $image);
    $image = str_replace ('@URL@', $this->image_url (), $image);
    return $image;
  }

  function validate ($random)
  {
    $this->__random = $random;

    $file_name = $this->__random_repository . '/' . $random;

    // Find out, whether the file exists
    $result = is_file ($file_name);

    // if the file exists, remember it.
    if ($result)
    {
      $this->__random_file = $file_name;
    }

    // the random string was valid, if and only if the corresponding
    // file existed.
    return $result;
  }

  function verify ($input, $random = False)
  {
    if (!$random)
    {
      $random = $this->__random;
    }
    $password_letters = $this->__alphabet;
    $password_length  = $this->__letters;

    // If the user input has the wrong lenght, it can't be correct.
    if (strlen ($input) != $password_length)
    {
      return False;
    }

    // Calculate the MD5 digest of the concatenation of secret key and
    // random string. The digest is a hex string.
    $encryption_base = $this->__secret . $random;
    // This extension is needed for secure use of optional parameters
    // In case of standard use we do not append the values, to be
    // compatible to existing implementations
    if(($password_letters  != 'abcdefghijklmnopqrstuvwxyz') || ($password_length != '6'))
    {
      $encryption_base = $encryption_base . ':' . $password_letters  . ':' . $password_length;
    }
    $digest = md5 ($encryption_base);

    // Check the password according to the rules from the first
    // positions of the digest.
    for ($pos = 0; $pos < $password_length; $pos++)
    {
      $letter_num
        = hexdec (substr ($digest, 2 * $pos, 2)) % strlen ($password_letters);

      // If the letter at the current position is wrong, the user
      // input isn't correct.
      if ($input[$pos] != $password_letters[$letter_num])
      {
        return False;
      }
    }

    // if the file exists, remove it.
    if ($this->__random_file)
    {
      unlink ($this->__random_file);
      unset ($this->__random_file);
    }

    // The user input was correct.
    return True;
  }

}

?>
