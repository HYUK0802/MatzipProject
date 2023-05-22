// 모든 HTML태그에 접근해서 show 하겠다.
HTMLElement.prototype.show = function () {
    this.classList.add('visible');
};

HTMLElement.prototype.hide = function() {
    this.classList.remove('visible');
};

HTMLInputElement.prototype.focusAndSelect = function () {
    this.focus();
    this.select();
};