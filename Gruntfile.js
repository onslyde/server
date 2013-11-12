/*global module:false*/
module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    // Metadata.

    //checkout panel and presentation modules
    shell: {
      gitclone: {
        command: ['git clone https://github.com/onslyde/onslyde-panel.git src/main/webapp/panel',
          'git clone https://github.com/onslyde/onslyde.git src/main/webapp/deck']
          .join('&&')
      }
    },

    //merge ambiguous files

    pkg: grunt.file.readJSON('package.json'),
    banner: '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
      '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
      '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
      '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
      ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %> */\n',
    // Task configuration.
    concat: {
      options: {
        banner: '<%= banner %>',
        stripBanners: true
      },
      basic_and_extras: {
        files: {
          'src/main/webapp/panel/js/panel/dist/onslyde-panel-1.0.0.js': ['src/main/webapp/panel/js/panel/onslyde-1.0.0.panel.js'],
          'src/main/webapp/panel/js/panel/dist/onslyde-remote-1.0.0.js': ['src/main/webapp/panel/js/panel/panel-remote.js',
            'src/main/webapp/panel/js/panel/gplus-oauth.js',
            'src/main/webapp/panel/js/panel/gracefulWebSocket.js',
            'src/main/webapp/panel/js/panel/fastclick.min.js'],
          'src/main/webapp/deck/js/deck/dist/onslyde-deck-1.0.0.js': ['src/main/webapp/deck/js/deck/deck.js','src/main/webapp/deck/js/deck/onslyde-1.0.0.deck.js'],
          'src/main/webapp/deck/js/deck/dist/onslyde-core-1.0.0.js': ['src/main/webapp/deck/js/deck/onslyde-1.0.0.deck.js'],
          'src/main/webapp/deck/js/deck/dist/onslyde-remote-1.0.0.js': ['src/main/webapp/deck/js/deck/gracefulWebSocket.js','js/deck/remote.js']
        }
      }
    },
    uglify: {
      options: {
        banner: '<%= banner %>'
      },
      primary : {
        files: {
          'src/main/webapp/panel/js/panel/dist/onslyde-remote-1.0.0.min.js': ['src/main/webapp/panel/js/panel/dist/onslyde-remote-1.0.0.js'],
          'src/main/webapp/panel/js/panel/dist/onslyde-panel-1.0.0.min.js': ['src/main/webapp/panel/js/panel/dist/onslyde-panel-1.0.0.js'],
          'src/main/webapp/deck/js/deck/dist/onslyde-remote-1.0.0.min.js': ['src/main/webapp/deck/js/deck/dist/onslyde-remote-1.0.0.js'],
          'src/main/webapp/deck/js/deck/dist/onslyde-core-1.0.0.min.js': ['src/main/webapp/deck/js/deck/dist/onslyde-core-1.0.0.js'],
          'src/main/webapp/deck/js/deck/dist/onslyde-deck-1.0.0.min.js': ['src/main/webapp/deck/js/deck/dist/onslyde-deck-1.0.0.js']
        }
      }
    },
    jshint: {
      options: {
        curly: true,
        eqeqeq: true,
        immed: true,
        latedef: true,
        newcap: true,
        noarg: true,
        sub: true,
        undef: true,
        unused: false,
        boss: true,
        eqnull: true,
        globals: {
          jQuery: true,
          'gapi' : true,
          '_gaq': true,
          'speak': true,
          'ws': true,
          window: true,
          document: true,
          onslyde: true,
          userObject: true,
          localStorage: true,
          WebSocket: true,
          setTimeout: true,
          clearTimeout: true,
          setInterval: true,
          clearInterval: true,
          XMLHttpRequest: true,
          location: true,
          console: true,
          navigator: true,
          getAttendees: true
        }
      },
      gruntfile: {
        src: 'Gruntfile.js'
      },
      lib_test: {
        src: ['src/main/webapp/panel/js/panel/*.js']
      }
    }
//    ,
//    nodeunit: {
//      files: ['test/**/*_test.js']
//    },
//    watch: {
//      gruntfile: {
//        files: '<%= jshint.gruntfile.src %>',
//        tasks: ['jshint:gruntfile']
//      },
//      lib_test: {
//        files: '<%= jshint.lib_test.src %>',
//        tasks: ['jshint:lib_test', 'nodeunit']
//      }
//    }
  });

  // These plugins provide necessary tasks.
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-nodeunit');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-watch');

  grunt.loadNpmTasks('grunt-shell');

  // Default task.
  grunt.registerTask('default', ['shell','jshint', 'concat', 'uglify']);

};
