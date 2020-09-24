import {Component, OnInit} from '@angular/core';
import {AuthService} from "../auth/auth.service";
import {Container} from "@angular/compiler/src/i18n/i18n_ast";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  data = [
    {
      name: 'Mario',
      email: 'mario@rossi.it'
    },
    {
      name: 'Giovanni',
      email: 'giovanni@verdi.it'
    }
  ];
  columns = ['name', 'email'];

  constructor(private authService: AuthService) {
    console.log(this.columns);
  }

  ngOnInit(): void {
  }

  id="tsparticles";

  particlesOptions = {
    fpsLimit: 120,
    particles: {
      number: {
        value: 160,
        density: {
          enable: true,
          value_area: 800
        }
        /*Speed*/
      },
      color: {
        value: "#ff0000",
        animation: {
          enable: true,
          speed: 2,
          sync: true
        }
      },
      /*Dots*/
      shape: {
        type: "circle",
        stroke: {
          width: 0
        },

        polygon: {
          nb_sides: 5
        }
        /*Line Speed*/
      },
      opacity: {
        value: 0.5,
        random: false,
        anim: {
          enable: false,
          speed: 3,
          opacity_min: 0.1,
          sync: false
        }
      },
      size: {
        value: 3,
        random: true,
        anim: {
          enable: false,
          speed: 1,
          size_min: 0.1,
          sync: false
        }
      },
      line_linked: {
        enable: true,
        distance: 80,
        color: "#ffffff",
        opacity: 0.4,
        width: 1
      },
      move: {
        enable: true,
        speed: 1.5,
        direction: "none",
        random: true,
        straight: false,
        out_mode: "out",
        attract: {
          enable: false,
          rotateX: 600,
          rotateY: 1200
        }
      },
      life: {
        duration: {
          sync: false,
          value: 50
        },
        count: 0,
        delay: {
          random: {
            enable: true,
            minimumValue: 0.5
          },
          value: 1
        }
      }
    },
    interactivity: {
      detect_on: "canvas",
      events: {
        onhover: {
          enable: true,
          mode: "repulse"
        },
        onclick: {
          enable: true,
          mode: "push"
        },
        resize: true
      },
      modes: {
        grab: {
          distance: 400,
          line_linked: {
            opacity: 1
          }
        },
        bubble: {
          distance: 400,
          size: 40,
          duration: 2,
          opacity: 0.8
        },
        repulse: {
          distance: 50
        },
        push: {
          particles_nb: 4
        },
        remove: {
          particles_nb: 2
        }
      }
    },
    retina_detect: true,
  };

  particlesLoaded(container): void {
    console.log(container);
  }

}
