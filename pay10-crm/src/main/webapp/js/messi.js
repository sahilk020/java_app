(function () {
    'use strict';

    function Messi(data, options) {

        var close;
        var _this = this;
        _this.options = jQuery.extend({}, Messi.prototype.options, options || {});

        // Resolve the viewport vs prototype option (prototype overrides viewport)
        if (this.options.position === Messi.prototype.options.position) {
            _this.options.position = _this.options.viewport;
        }

        // Prepare the item
        _this.messi = jQuery(_this.template);
        
        _this.setContent(data);

        // Adjust the title
        if (_this.options.title === null) {

            jQuery('.messi-titlebox', _this.messi)
                .remove();
           

        } else {

            jQuery('.messi-title', _this.messi)
                .append(_this.options.title);
            

            if (_this.options.buttons.length === 0 && !_this.options.autoclose) {

                // Close button required
                close = jQuery('<span class="messi-closebtn"></span>');
                close.bind('click', function () {
                    _this.hide();
                });

                jQuery('.messi-titlebox', this.messi)
                    .prepend(close);

            }

            if (_this.options.titleClass !== null) {
                jQuery('.messi-titlebox', this.messi)
                    .addClass(_this.options.titleClass);
            }

        }

        // Adjust the width
        if (_this.options.width !== null) {
            jQuery('.messi-box', _this.messi)
                .css('width', _this.options.width);
        }

        // Prepare the buttons
        if (_this.options.buttons.length > 0) {
            for (var i = 0; i < _this.options.buttons.length; i++) {
                var btnbox = jQuery('<div>', {'class':'messi-btnbox'})
                    .css('width', parseInt(100/_this.options.buttons.length, 10) + '%');
                var cls = (_this.options.buttons[i]['class']) ? _this.options.buttons[i]['class'] : '';
                var btn = jQuery('<button>', {
                    href: '#',
                    'class': 'btn ' + cls,
                    value: _this.options.buttons[i].val,
                    'click': function () {
                        var value = $(this).val();

                        if (typeof _this.options.callback === 'function') {
                            if (_this.options.callback(value) === false) {
                                return this;
                            }
                        }
                        $('.messi').remove();//Added to close pop up when close button is clicked
                        _this.hide();
                    }
                }).text(_this.options.buttons[i].label);

                btnbox.append(btn);
                jQuery('.messi-actions', this.messi).append(btnbox);

            }

        } else {

            jQuery('.messi-footbox', this.messi)
                .remove();

        }

        // Prepare the close button automatically
        if (_this.options.buttons.length === 0 && _this.options.title === null && !_this.options.autoclose) {

            if (_this.options.closeButton) {
                close = jQuery('<span class="messi-closebtn"></span>');
                close.bind('click', function () {
                	//alert('hi1');
                	
                	$('.messi').remove();
                	$('.messi,.modal').remove();
                    _this.hide();
                });

                jQuery('.messi-content', this.messi)
                    .prepend(close);

            }

        }

        // Activate the modal screen
        if (_this.options.modal) {
            _this.modal = jQuery('<div class="modal" ></div>')
            .css({
                opacity: _this.options.modalOpacity,
                width: jQuery(document).width(),
                height: jQuery(document).height(),
                position: 'fixed',
                'z-index': _this.options.zIndex + jQuery('.messi').length
            })
            .appendTo(document.body);
        }

        // Show the message
        if (_this.options.show) { _this.show(); }

        // Control the resizing of the display
        jQuery(window).bind('resize scroll', function () {
            _this.resize();
        });

        // Configure the automatic closing
        if (_this.options.autoclose !== null) {
            setTimeout(function () {
                var value = jQuery.data(this, 'value');
                var after = (_this.options.callback !== null) ? function () {
                        _this.options.callback(value);
                    } : null;
                _this.hide();
            }, _this.options.autoclose, this);
        }

        return _this;

    }

    Messi.prototype = {

        options: {
            animate: { open: 'bounceIn', close: 'bounceOut' },  // default animation (disable by setting animate: false)
            autoclose: null,                                    // autoclose message after 'x' miliseconds, i.e: 5000
            buttons: [],                                        // array of buttons, i.e: [{id: 'ok', label: 'OK', val: 'OK'}]
            callback: null,                                     // callback function after close message
            center: false,                                       // center message on screen
            closeButton: true,                                  // show close button in header title (or content if buttons array is empty).
            height: 'auto',                                     // content height
            title: null,                                        // message title
            titleClass: null,                                   // title style: info, warning, success, error
            margin: 0,                                          // enforce a minimal viewport margin the dialog cannot move outside, set to zero to disable
            modal: false,                                       // shows message in modal (loads background)
            modalOpacity: 0.5,                                  // modal background opacity
            padding: '10px',                                    // content padding
            position: { top: '5%', left: '20%' },              // if center: false, sets X and Y position
            show: true,                                         // show message after load
            unload: true,                                       // unload message after hide
            viewport: { top: '5%', left: '20%' },              // deprecated, see position
            // width: '70%',                                     // message width
            zIndex: 99999                                       // first dialog z-index
        },
        template: '<div class="messi"  id="popup" style="overflow-y: auto;display: block;"><div class="messi-box  innerpopupDv"><div class="messi-titlebox"><span class="messi-title"></span></div><div class="modal-content"></div></div></div>',
        content: '<div></div>',
        visible: false,

        setContent: function (data) {
            jQuery('.modal-content', this.messi)
                .css({
                    padding: this.options.padding,
                    height: this.options.height
                })
                .empty()
                .append(data);
        },

        center: function () {
            this.messi.css({
                top: ((jQuery(window).height() - this.messi.height()) / 2),
                left: ((jQuery(window).width() - this.messi.width()) / 2)
            });

            return this;
        },

        show: function () {

            if (this.visible) { return; }

            if (this.messi.parent().length === 0) {
                // or unload in case of first call
                if (this.modal) {
                    this.modal.appendTo(document.body);
                }
                this.messi.appendTo(document.body);
            }

            if (this.modal) {
                this.modal.show();
            }

            // positioning
            this.messi.css({
                top: this.options.position.top,
                left: this.options.position.left
            });

            this.messi.css({
                'zIndex': this.options.zIndex + jQuery('.messi').length
            });

            // animation
            if (this.options.animate) {
                this.messi.addClass('animated '+this.options.animate.open);
            }

            this.messi.show();

            // Get the center of the screen if the center option is on
            if (this.options.center) {
                this.center();
            } else {
                this.enforceMargin();
            }

            // Cancel the scroll
            //document.documentElement.style.overflow = "hidden";

            this.visible = true;

        },

        hide: function () {

            if (!this.visible) { return; }
            var _this = this;

            if (this.options.animate) {
                this.messi.one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function() {
                    _this.visible = false;
                    if (_this.options.unload) {
                        _this.unload();
                    }
                });

                this.messi.removeClass(this.options.animate.open).addClass(this.options.animate.close);
            } else {
                this.messi.animate({
                    opacity: 0
                }, 300, function () {
                    if (_this.options.modal) {
                        _this.modal.css({
                            display: 'none'
                        });
                    }
                    _this.messi.css({
                        display: 'none'
                    });

                    // Reactivate the scroll
                    //document.documentElement.style.overflow = "visible";
                    _this.visible = false;
                    if (_this.options.unload) {
                        _this.unload();
                    }
                });
            }

            return this;

        },

        resize: function () {
            if (this.options.modal) {
                jQuery('.modal')
                    .css({
                        width: jQuery(document).width(),
                        height: jQuery(document).height()
                    });
            }

            if (this.options.center) {
                this.center();
            } else if(this.options.margin > 0) {
                this.enforceMargin();
            }
        },

        toggle: function () {
            this[this.visible ? 'hide' : 'show']();
            return this;
        },

        unload: function () {
            if (this.visible) {
                this.hide();
            }

            jQuery(window)
                .unbind('resize scroll');

            if (this.modal) {
                this.modal.remove();
                
            }

            this.messi.remove();
        },

        // When the dialog is outside the viewport, move it back in.
        // options.viewport is the center point of the dialog
        enforceMargin: function () {
            if (!this.options.margin) { return; }

            var $window = jQuery(window);

            // Backward compatibility hack - remove in version 2.1
            var x = this.max(
                parseInt(this.options.viewport.left, 10),
                parseInt(this.options.position.left, 10)
            );
            var y = this.max(
                parseInt(this.options.viewport.top, 10),
                parseInt(this.options.position.top, 10)
            );

            // When the popup is too far on the right, move left
            if (x + this.messi.width() + this.options.margin > $window.width()) {
                x = $window.width() - this.options.margin - this.messi.width();
            }

            // When the popup is too far down, move up
            if (y + this.messi.height() + this.options.margin > $window.height()) {
                y = $window.height() - this.options.margin - this.messi.height();
            }

            // When the popup is too far to the left, move right
            if (x < this.options.margin) {
                x = this.options.margin;
            }

            // When the popup is too far up, move down
            if (y < this.options.margin) {
                y = this.options.margin;
            }

            this.messi.css({ left: x, top: y });
        },

        jqueryize: function() {
            return this.messi;
        },

        max: function (a, b) {
            if (a > b) { return a; }
            else { return b; }
        },

    };

    // Preserve backward compatibility
    window.Messi = Messi;

})();