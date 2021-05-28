
/***********created-summary-view*********/
$(document).on('click', '.trgcollapse', function (e) {
  e.preventDefault();
  var val = $(this).attr('id');
    $('.trg-show').addClass('show');
    $('.trg-open').removeClass('collapsed');
    $(".trg-open").attr("aria-expanded","true");
  if ($(this).hasClass("active")) {
    $(this).removeClass('active');
    $('.slider-wrap-up').removeClass('active');
  } else {
    $('.trgcollapse').removeClass('active');
    $(this).addClass('active');
    $('.slider-wrap-up').removeClass('active');
    $('.slider-wrap-up.' + val).addClass('active');
  }
});
/****collapse all****/
$(document).on('click', '.collapseall', function (e) {
  e.preventDefault();
  var val = $(this).attr('id');
  if ($(this).hasClass("active")) {
    $(this).removeClass('active');
    $('.allshow').removeClass('show');
    $(".allcollapsed").attr("aria-expanded","false");
    $('.allcollapsed').addClass('collapsed');
  } else {
    $(this).addClass('active');
    $('.allshow').addClass('show');
    $('.allcollapsed').removeClass('collapsed');
    $(".allcollapsed").attr("aria-expanded","true");
  }
});
/*********Order-List-Collapse********/
$(document).on('click', '.openAll', function (e) {
  e.preventDefault();
  var href = $(this).attr('id');
    if($(this).hasClass("active")){
    }
    else{
      $('.closeAll').removeClass('active');
      $(this).addClass('active');
      $("." +href).attr("aria-expanded","true");
      $("." +href).removeClass('collapsed');
      $('.expandBot').addClass('show');
    }
});
$(document).on('click', '.closeAll', function (e) {
  e.preventDefault();
    if($(this).hasClass("active")){
    }
    else{
      $('.openAll').removeClass('active');
      $(this).addClass('active');
      $('.expandBot').removeClass('show');
      $('.expandSuit').attr("aria-expanded","false");
      $('.expandSuit').addClass('collapsed');
    }
});
$(document).on('click', '.expandSuit', function (e) {
  e.preventDefault();
  $('.openAll').removeClass('active');
  $('.closeAll').removeClass('active');
});
/*******Order-list********/
// $(document).on('click', '.order-list', function (e) {
//   e.preventDefault();
//   //var attribute = $(this).attr("id");
//   var href = $(this).attr('id');
//   //console.log(href);
//   if ($(this).hasClass("active")) {
//     $(this).removeClass('active');
//     $("." +href).removeClass('show');
//     $(this).attr("aria-expanded","false");
//     $(this).addClass('collapsed');
//   } else {
//     $(".order-content").removeClass('show');
//     $(this).addClass('active');
//     $("." +href).addClass('show');
//     $(this).removeClass('collapsed');
//     $(this).attr("aria-expanded","true");
//   }
// });

$(document).on('click', '.trgslide', function (e) {
  e.preventDefault();
  var val = $(this).attr('id');
  if ($(this).hasClass("active")) {
    $(this).removeClass('active');
    $('.slider-wrap').removeClass('active');
  } else {
    $('.trgslide').removeClass('active');
    $(this).addClass('active');
    $('.slider-wrap').removeClass('active');
    $('.slider-wrap.' + val).addClass('active');
  }
});
/****slider-close****/
$(document).on('click', '.closepop', function (e) {
  e.preventDefault();
  $(this).addClass('close');
  setTimeout(function(){ $('.closepop').removeClass('close'); }, 1000)
});


// /*****slider****/
// $(document).on('click', '.trgslide', function (e) {
//   e.preventDefault();
//   var val = $(this).attr('id');
//   if ($(this).hasClass("active")) {
//     $(this).removeClass('active');
//     $('.slider-wrap').removeClass('active');
//   } else {
//     $('.trgslide').removeClass('active');
//     $(this).addClass('active');
//     $('.slider-wrap').removeClass('active');
//     $('.slider-wrap.' + val).addClass('active');
//   }

// });
// /****slider-close****/
// $(document).on('click', '.closepop', function (e) {
//   e.preventDefault();
//   $('.trgslide').removeClass('active');
//   $('.slider-wrap').removeClass('active');
// });
/*****file upload****/
// let state = {};
// function updateState(newState) {
//   state = { ...state, ...newState };
//   // console.log(state);
// }
// $("#upload").change(function (e) {
//   let files = document.getElementsByTagName("input")[4].files;
//   // console.log(files);
//   let filesArr = Array.from(files);
//   updateState({ files: files, filesArr: filesArr });
//   renderFileList();
// });
// $(".filess").on("click", "i", function (e) {
//   let key = $(this)
//     .parent()
//     .attr("key");
//   let curArr = state.filesArr;
//   curArr.splice(key, 1);
//   updateState({ filesArr: curArr });
//   renderFileList();
// });
// function renderFileList() {
//   let fileMap = state.filesArr.map((file, index) => {
//     return `<li key="${index}"><div class="text-center"><i class="fa fa-file" aria-hidden="true"></i></div><div class="pt-1">${
//       file.name
//       }</div> </li><i class="fa fa-times-circle" aria-hidden="true"></i>`;
//   });
//   $(".file-list").html(fileMap);
// }
/***roadmap nav-bar******/

// function roadMap() {

//   var navListItems = $('div.setup-panel div a'),
//   allWells = $('.setup-content'),
//   allNextBtn = $('.nextBtn');
//   allWells.hide();
//   navListItems.click(function (e) {
//       e.preventDefault();
//       var $target = $($(this).attr('href')),
//           $item = $(this);
//       var nextStepWizard = $(this).attr('id');
//       if(nextStepWizard == 1){
//           $('.stepwizard .progress-bar').animate({width:'0%'},0);
//           $('#1').addClass('visited-1');
//           $('#2').removeClass('visited-2');
//           $('#3').removeClass('visited-3');
//       }
//       if(nextStepWizard == 2){
//           $('.stepwizard .progress-2').animate({width:'100%'},0);
//           $('.stepwizard .progress-3').animate({width:'0%'},0);
//           $('#1').addClass('visited-1');
//           $('#2').addClass('visited-2');
//           $('#3').removeClass('visited-3');
//       }
//       if(nextStepWizard == 3){
//           $('.stepwizard .progress-3').animate({width:'100%'},0);
//           $('.stepwizard .progress-2').animate({width:'100%'},0);
//           $('#1').addClass('visited-1');
//           $('#2').addClass('visited-2');
//           $('#3').addClass('visited-3');
//       }
//       if (!$item.hasClass('disabled')) {
//           navListItems.removeClass('btn-active').addClass('btn-default');
//           $item.addClass('btn-active');
//           allWells.hide();
//           $target.show();
//           //$target.find('input:eq(0)').focus();
//       }
//   });

//   allNextBtn.click(function () {
//       var curStep = $(this).closest(".setup-content"),
//           curStepBtn = curStep.attr("id"),
//           nextStepWizard = $('div.setup-panel div a[href="#' + curStepBtn + '"]').parent().next().children("a");
//           nextStepWizard.removeClass('disabled').trigger('click');
//   });
//   $('div.setup-panel div a.btn-active').trigger('click');
// }
/***Select all***/
var map = [];
// console.log(map);
$(document).on('change', '.selectBots', function (e) {
  e.preventDefault();
  var val = $(this).attr('id');
  var value = "items-"+val;
  $(".items-"+val).prop("checked", $(this).prop("checked"));
  var count = 0;
    $(".items-"+val).each(function(i, obj){
      count++;
    })
  if ($(this).prop("checked")) {
    const found = map.some(el => el.key === value);
    if(!found){
      map.push({ key:value, value: 0 })
    };
    for (var i in map) {
      if (map[i].key == value) {
        map[i].value = count;
        break; //Stop this loop, we found it!
    }
  }
}
  else{
    for (var i in map) {
      if (map[i].key == value) {
        map[i].value = 0;
        break; //Stop this loop, we found it!
    }
    }
  }
  // console.log(map);
});

$(document).on('change', '.botcollection', function (e) {
  e.preventDefault();
  var inc;
  var val = $(this).attr('id');
  var value = $(this).attr('class');
  var sliceValue = (value).slice(14,28);
  var count = 0;
    $(`.${sliceValue}`).each(function(i, obj){
      count++;
    })
    const found = map.some(el => el.key === sliceValue);
    if(!found){
      map.push({ key:sliceValue, value: 0 })
    };
    // console.log(map);
    if (!$(this).prop("checked")) {
         inc = false;
         selectCheck(count,inc,sliceValue,map,val);
      }
  else{
      inc=true;
      selectCheck(count,inc,sliceValue,map,val);
  }
});
function selectCheck(count, inc, sliceValue,map,val){
  if(inc){
    for (var i in map) {
      if (map[i].key == sliceValue) {
          map[i].value = map[i].value+1;
          var incValue = map[i].value;
          break; //Stop this loop, we found it!
      }
    }
  }
  else{
    for (var i in map) {
      if (map[i].key == sliceValue && map[i].value>0) {
          map[i].value = map[i].value-1;
          var decValue = map[i].value;
          break; //Stop this loop, we found it!
      }
    }
  }
  if(incValue == count){
    $(".selectBots." +val).prop("checked", true);
  }
  else{
    $(".selectBots." +val).prop("checked", false);
  }
}
// sorting icon
$(document).on('click', '.title', function (e) {
  e.preventDefault();
  var val = $(this).attr('id');
            if ($(this).hasClass("active")) {
                $(this).removeClass('active');
                $(this).addClass('inactive');
            }
             else if ($(this).hasClass("inactive")) {
                $('.title').removeClass('active');
                $('.title').removeClass('inactive');
                $(this).addClass('active');
            }
            else{
              $('.title').removeClass('active');
              $('.title').removeClass('inactive');
              $(this).addClass('inactive');
            }
});

 $(document).on("click",'.automationTabs',function(e) {
  $('.title').removeClass('active');
              $('.title').removeClass('inactive');
 });
 $(document).on("click", '.clearActiveRefresh',function(e) {
  $('.title').removeClass('active');
              $('.title').removeClass('inactive');
 });
//  $(document).on("click", '.mainClearActive',function(e) {
//   $('.title').removeClass('active');
//               $('.title').removeClass('inactive');
//  });


